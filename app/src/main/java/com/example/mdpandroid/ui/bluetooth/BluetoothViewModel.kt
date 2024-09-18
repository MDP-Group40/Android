package com.example.mdpandroid.ui.bluetooth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdpandroid.domain.BluetoothController
import com.example.mdpandroid.domain.BluetoothDevice
import com.example.mdpandroid.domain.BluetoothDeviceDomain
import com.example.mdpandroid.domain.ConnectionResult
import com.example.mdpandroid.domain.BluetoothMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
open class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
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
        _isBluetoothEnabled.value = bluetoothController.isBluetoothEnabled()  // Update value after toggling
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

    fun connectToDevice(device: BluetoothDeviceDomain) {

        _state.update {
            it.copy(
                isConnecting = true,
                connectingDevice = device,
                failedDevice = null,
                errorMessage = null  // Clear error message
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                withTimeout(10000) {  // Timeout of 10 seconds for connection
                    bluetoothController.connectToDevice(device)
                        .onEach { result ->
                            when (result) {
                                is ConnectionResult.ConnectionEstablished -> {
                                    _state.update {
                                        it.copy(
                                            isConnecting = false,
                                            isConnected = true,
                                            connectingDevice = null,
                                            connectedDevice = BluetoothDevice(device.name, device.address),
                                            failedDevice = null  // Clear any previous failed state
                                        )
                                    }
                                }
                                is ConnectionResult.Error -> {
                                    _state.update {
                                        it.copy(
                                            isConnecting = false,
                                            connectingDevice = null,
                                            failedDevice = device,  // Set the failed device
                                            errorMessage = result.message  // Set the error message
                                        )
                                    }
                                }
                                is ConnectionResult.TransferSucceeded -> {
                                    _state.update { currentState ->
                                        currentState.copy(
                                            messages = currentState.messages + result.message
                                        )
                                    }
                                }
                            }
                        }
                        .launchIn(viewModelScope)
                }
            } catch (e: TimeoutCancellationException) {
                _state.update {
                    it.copy(
                        isConnecting = false,
                        connectingDevice = null,
                        failedDevice = device,
                        errorMessage = "Connection timed out"
                    )
                }
            }
        }
    }

    // Disconnect from the current device
    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false
            )
        }
    }

    // Wait for incoming Bluetooth connections
    fun waitForIncomingConnections() {
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
                _state.update { it.copy(messages = it.messages + sentMessage) }
            } else {
                // Log failure if the message could not be sent
                Log.e("BluetoothViewModel", "Failed to send message: $bluetoothMessage")
            }
        }
    }

    // Handle incoming messages
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
                }
                is ConnectionResult.TransferSucceeded -> {
                    _state.update {
                        it.copy(
                            messages = it.messages + result.message
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message
                        )
                    }
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
}
