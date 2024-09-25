package com.example.mdpandroid.ui.bluetooth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdpandroid.domain.BluetoothController
import com.example.mdpandroid.domain.BluetoothDevice
import com.example.mdpandroid.domain.BluetoothDeviceDomain
import com.example.mdpandroid.domain.BluetoothMessage
import com.example.mdpandroid.domain.ConnectionResult
import com.example.mdpandroid.domain.ImageMessage
import com.example.mdpandroid.domain.MovementMessage
import com.example.mdpandroid.ui.car.CarViewModel
import com.example.mdpandroid.ui.shared.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
open class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
): ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            messages = if (state.isConnected) state.messages else emptyList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var deviceConnectionJob: Job? = null

    private val _isBluetoothEnabled = MutableStateFlow(bluetoothController.isBluetoothEnabled())
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error ->
            _state.update { it.copy(errorMessage = error) }
        }.launchIn(viewModelScope)
    }

    // Toggle Bluetooth ON/OFF
    fun toggleBluetooth() {
        if (_isBluetoothEnabled.value) {
            bluetoothController.disableBluetooth()
        } else {
            bluetoothController.enableBluetooth()
        }
        _isBluetoothEnabled.value =
            bluetoothController.isBluetoothEnabled()  // Update value after toggling
    }

    // Start scanning for devices
    fun startScan() {
        if (!_isScanning.value) {
            viewModelScope.launch(Dispatchers.IO) {
                bluetoothController.startDiscovery()
                _isScanning.value = true
                _state.update { it.copy(message = "Scanning started...") }
            }
        }
    }

    // Stop scanning for devices
    fun stopScan() {
        if (_isScanning.value) {
            viewModelScope.launch(Dispatchers.IO) {
                bluetoothController.stopDiscovery()
                _isScanning.value = false
                _state.update { it.copy(message = "Scanning stopped...") }
            }
        }
    }

    fun connectToDevice(
        device: BluetoothDeviceDomain,
        sharedViewModel: SharedViewModel,
        carViewModel: CarViewModel) {
        Log.d("BluetoothViewModel", "Starting connection to device: ${device.address}")

        _state.update {
            it.copy(
                isConnecting = true,
                connectingDevice = device,
                failedDevice = null,
                errorMessage = null
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                withTimeout(10000) {
                    bluetoothController.connectToDevice(device)
                        .onEach { result ->
                            when (result) {
                                is ConnectionResult.ConnectionEstablished -> {
                                    Log.d("BluetoothViewModel", "Connection established with device: ${device.address}")

                                    _state.update {
                                        it.copy(
                                            isConnecting = false,
                                            isConnected = true,
                                            connectingDevice = null,
                                            connectedDevice = BluetoothDevice(
                                                device.name,
                                                device.address
                                            ),
                                            failedDevice = null
                                        )
                                    }
                                }

                                is ConnectionResult.Error -> {
                                    Log.e("BluetoothViewModel", "Connection error with device: ${device.address}, message: ${result.message}")

                                    _state.update {
                                        it.copy(
                                            isConnecting = false,
                                            connectingDevice = null,
                                            failedDevice = device,
                                            errorMessage = result.message
                                        )
                                    }
                                    // Reflect the UI for reconnection attempt
                                    onConnectionLost()  // Retry connection when connection fails
                                }

                                is ConnectionResult.TransferSucceeded -> {
                                    Log.d("BluetoothViewModel", "Message transfer succeeded: ${result.message}")

                                    _state.update { currentState ->
                                        val updatedMessages = currentState.messages + result.message // Accumulate received messages
                                        Log.d("BluetoothViewModel", "Updated message list (after receiving): $updatedMessages")

                                        currentState.copy(messages = updatedMessages) // Properly update state with accumulated messages
                                    }

                                    // see if message is regarding image
                                    handleReceivedImageMessage(result.message, sharedViewModel)

                                    // see if message is regarding the robot movement
                                    handleReceivedMovementMessage(result.message, carViewModel)

                                }
                            }
                        }
                        .launchIn(viewModelScope)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("BluetoothViewModel", "Connection timed out for device: ${device.address}")

                _state.update {
                    it.copy(
                        isConnecting = false,
                        connectingDevice = null,
                        failedDevice = device,
                        errorMessage = "Connection timed out"
                    )
                }
                onConnectionLost()  // Retry connection after failure
            }
        }
    }

    // Reconnect to the last paired device if the connection is lost
    fun reconnectToLastPairedDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isConnecting = true) }  // Show loading icon during reconnection
            when (val result = bluetoothController.reconnectToLastDevice()) {
                is ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            failedDevice = null,
                            errorMessage = null  // Clear error message
                        )
                    }
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message,
                            failedDevice = bluetoothController.lastConnectedDevice  // Track the failed device
                        )
                    }
                }

                else -> {
                    // Handle other cases like TransferSucceeded, or do nothing
                }
            }
        }
    }

    // Automatically attempt to reconnect if the connection is lost
    private fun onConnectionLost() {
        _state.update {
            it.copy(
                isConnected = false,
                errorMessage = "Connection lost, attempting to reconnect..."
            )
        }
        reconnectToLastPairedDevice()  // Try to reconnect
    }

    // Wait for incoming Bluetooth connections
    private fun waitForIncomingConnections() {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = viewModelScope.launch(Dispatchers.IO) {
            bluetoothController.startBluetoothServer()
                .listen()  // Listen for incoming connections and messages
        }
    }

    // Function to send a BluetoothMessage (Text, Info, or Obstacle)
    fun sendMessage(bluetoothMessage: BluetoothMessage) {
        // Log the message being sent
        Log.d("BluetoothViewModel", "Sending message: $bluetoothMessage")

        viewModelScope.launch(Dispatchers.IO) {
            val sentMessage = bluetoothController.trySendMessage(bluetoothMessage)
            if (sentMessage != null) {
                // Log the sent message after it has been successfully sent
                Log.d("BluetoothViewModel", "Message sent successfully: $sentMessage")

                // Update the state with the sent message
                _state.update { currentState ->
                    val updatedMessages =
                        currentState.messages + sentMessage  // Accumulate messages properly
                    Log.d(
                        "BluetoothViewModel",
                        "Updated message list (after sending): $updatedMessages"
                    )

                    currentState.copy(messages = updatedMessages)  // Properly update state with accumulated messages
                }

                Log.d("BluetoothViewModel", "Message Sent: $sentMessage")
            } else {
                // Log failure if the message could not be sent
                Log.e("BluetoothViewModel", "Failed to send message: $bluetoothMessage")
            }
        }
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                    Log.d(
                        "BluetoothViewModel",
                        "Connection established. Messages: ${_state.value.messages}"
                    )
                }

                is ConnectionResult.TransferSucceeded -> {
                    _state.update {
                        it.copy(
                            messages = it.messages + result.message
                        )
                    }
                    // Log the updated messages after a new message is received
                    Log.d("BluetoothViewModel", "Message sent: ${result.message}")
                    Log.d("BluetoothViewModel", "Current messages: ${_state.value.messages}")
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message
                        )
                    }
                    Log.e("BluetoothViewModel", "Error occurred: ${result.message}")
                }
            }
        }
            .catch {
                bluetoothController.closeConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                    )
                }
                Log.e("BluetoothViewModel", "Flow error: Connection closed.")
            }
            .launchIn(viewModelScope)
    }

    // Clear the current message
    private fun clearMessage() {
        _state.update { it.copy(message = null) }
    }

    override fun onCleared() {
        super.onCleared()
        clearMessage()
        bluetoothController.closeConnection()  // Call the close function here
    }

    private fun handleReceivedImageMessage(message: BluetoothMessage, sharedViewModel: SharedViewModel) {
        // Check if the message is an instance of ImageMessage
        if (message is ImageMessage) {
            // Log the received message
            Log.d("BluetoothViewModel", "Received ImageMessage: $message")

            // Pass the targetID and numberOnObstacle to the sharedViewModel
            sharedViewModel.setNumberOnObstacle(message.targetId, message.numberOnObstacle)
        }
    }

    private fun handleReceivedMovementMessage(message: BluetoothMessage, carViewModel: CarViewModel) {
        // Check if the message is an instance of MovementMessage
        if (message is MovementMessage) {
            // Log the received message
            Log.d("BluetoothViewModel", "Received MovementMessage: $message")

            // Enqueue the movement message into the carViewModel's queue
            carViewModel.enqueueMovementMessage(message)
        }
    }
}
