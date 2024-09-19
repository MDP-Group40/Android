package com.example.mdpandroid.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import com.example.mdpandroid.domain.BluetoothController
import com.example.mdpandroid.domain.BluetoothDeviceDomain
import com.example.mdpandroid.domain.BluetoothMessage
import com.example.mdpandroid.domain.ConnectionResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context
) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var dataTransferService: BluetoothDataTransferService? = null

    private var lastConnectedDevice: BluetoothDeviceDomain? = null  // Track the last paired device

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice !in devices) devices + newDevice else devices
        }
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        } else {
            _isConnected.update { false }
            Log.d("Bluetooth", "Connection lost. Attempting to reconnect...")

            // Attempt to reconnect using retry logic
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Connection lost, attempting to reconnect...")
                val deviceDomain = bluetoothDevice.toBluetoothDeviceDomain()
                val connectionResult = retryConnectionWithBackoff(deviceDomain)

                // Handle the result of the reconnection attempt
                if (connectionResult is ConnectionResult.ConnectionEstablished) {
                    Log.d("Bluetooth", "Reconnection successful")
                    _isConnected.update { true }
                } else {
                    Log.e("Bluetooth", "Reconnection failed")
                    _errors.emit("Reconnection failed")
                }
            }
        }
    }

    private var isFoundDeviceReceiverRegistered = false
    private var isBluetoothStateReceiverRegistered = false

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    init {
        updatePairedDevices()
        if (!isBluetoothStateReceiverRegistered) {
            context.registerReceiver(
                bluetoothStateReceiver,
                IntentFilter().apply {
                    addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                    addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                    addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                }
            )
            isBluetoothStateReceiverRegistered = true
        }
    }

    override fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    override fun enableBluetooth() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Permission to connect to Bluetooth is not granted")
            }
            return
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Requesting to enable Bluetooth...")
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Bluetooth is already enabled")
            }
        }
    }

    override fun disableBluetooth() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Permission to connect to Bluetooth is not granted")
            }
            return
        }

        if (bluetoothAdapter?.isEnabled == true) {
            val intent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Guiding user to Bluetooth settings to disable Bluetooth...")
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Bluetooth is already disabled")
            }
        }
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN) ||
            !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Required permissions not granted")
            }
            return
        }

        if (bluetoothAdapter?.isEnabled == false) {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Please enable Bluetooth")
            }
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter!!.cancelDiscovery()
            }

            if (!isFoundDeviceReceiverRegistered) {
                context.registerReceiver(
                    foundDeviceReceiver,
                    IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
                        addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                        addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                    }
                )
                isFoundDeviceReceiverRegistered = true
            }

            try {
                val discoveryStarted = bluetoothAdapter?.startDiscovery() == true
                if (!discoveryStarted) {
                    CoroutineScope(Dispatchers.IO).launch {
                        _errors.emit("Failed to start discovery")
                    }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.IO).launch {
                    _errors.emit("Error during discovery: ${e.message}")
                }
            }

            updatePairedDevices()
        }
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) return

        bluetoothAdapter?.cancelDiscovery()
        if (isFoundDeviceReceiverRegistered) {
            context.unregisterReceiver(foundDeviceReceiver)
            isFoundDeviceReceiverRegistered = false
        }
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "MDP_android",
                UUID.fromString(SERVICE_UUID)
            )

            var shouldLoop = true
            while (shouldLoop) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    null
                }

                if (currentClientSocket != null) {
                    Log.d("Bluetooth", "Client connected, initializing DataTransferService")

                    // Initialize the DataTransferService after accepting the connection
                    dataTransferService = BluetoothDataTransferService(currentClientSocket!!)

                    emit(ConnectionResult.ConnectionEstablished)

                    // Start listening for incoming messages after the connection is established
                    emitAll(
                        dataTransferService!!.listenForIncomingMessages().map { message ->
                            ConnectionResult.TransferSucceeded(message)
                        }
                    )
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }


    // Method to start a connection with retry logic
    override suspend fun reconnectToLastDevice(retries: Int): ConnectionResult {
        val device = lastConnectedDevice ?: return ConnectionResult.Error("No device to reconnect")
        return retryConnectionWithBackoff(device, retries)
    }

    // Retry logic with backoff
    private suspend fun retryConnectionWithBackoff(
        device: BluetoothDeviceDomain,
        retries: Int = 3
    ): ConnectionResult {
        var attempt = 0
        while (attempt < retries) {
            try {
                return connectToDeviceOnce(device).single()
            } catch (e: Exception) {
                Log.e("Bluetooth", "Retry connection attempt ${attempt + 1} failed: ${e.message}")
                delay(1000L * (attempt + 1))  // Delay with backoff
                attempt++
            }
        }
        return ConnectionResult.Error("Failed after $retries retries")
    }

    private fun connectToDeviceOnce(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            try {
                currentClientSocket = bluetoothAdapter
                    ?.getRemoteDevice(device.address)
                    ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))

                stopDiscovery()

                currentClientSocket?.let { socket ->
                    withTimeoutOrNull(10000) {
                        socket.connect()
                    } ?: throw IOException("Connection timed out")

                    lastConnectedDevice = device  // Store the last connected device

                    // Initialize the DataTransferService after the connection is established
                    dataTransferService = BluetoothDataTransferService(socket)

                    emit(ConnectionResult.ConnectionEstablished)
                } ?: run {
                    emit(ConnectionResult.Error("Failed to create client socket"))
                }
            } catch (e: IOException) {
                currentClientSocket?.close()
                emit(ConnectionResult.Error("Connection was interrupted: ${e.message}"))
            }
        }
    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            Log.d("Bluetooth", "Attempting to connect to device: ${device.address}")

            // Save the last connected device
            lastConnectedDevice = device

            try {
                currentClientSocket = bluetoothAdapter
                    ?.getRemoteDevice(device.address)
                    ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))

                stopDiscovery()

                currentClientSocket?.let { socket ->
                    withTimeoutOrNull(10000) {
                        socket.connect()
                    } ?: throw IOException("Connection timed out")

                    // Initialize the DataTransferService after the connection is established
                    dataTransferService = BluetoothDataTransferService(socket)

                    Log.d("Bluetooth", "Connection established and DataTransferService initialized.")

                    emit(ConnectionResult.ConnectionEstablished)

                    // Start listening for incoming messages after establishing the connection
                    emitAll(
                        dataTransferService!!.listenForIncomingMessages().map { message ->
                            Log.d("Bluetooth", "Message transfer succeeded: $message") // Log the successful transfer
                            ConnectionResult.TransferSucceeded(message)
                        }

                    )
                } ?: run {
                    emit(ConnectionResult.Error("Failed to create client socket"))
                }
            } catch (e: IOException) {
                currentClientSocket?.close()
                emit(ConnectionResult.Error("Connection was interrupted: ${e.message}"))
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun trySendMessage(bluetoothMessage: BluetoothMessage): BluetoothMessage? {
        Log.d("BluetoothController", "Attempting to send message: $bluetoothMessage")

        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            Log.e("BluetoothController", "Permission denied: BLUETOOTH_CONNECT permission is missing.")
            return null
        }

        if (dataTransferService == null) {
            Log.e("BluetoothController", "DataTransferService is not initialized. Cannot send message.")
            return null
        }

        return withContext(Dispatchers.IO) {
            Log.d("BluetoothController", "Sending message on IO thread...")

            val isSent = dataTransferService?.sendBluetoothMessage(bluetoothMessage) ?: false

            if (isSent) {
                Log.d("BluetoothController", "Message sent successfully: $bluetoothMessage")
                bluetoothMessage
            } else {
                Log.e("BluetoothController", "Failed to send message: $bluetoothMessage")
                null
            }
        }
    }

    override fun closeConnection() {
        CoroutineScope(Dispatchers.IO).launch(NonCancellable) {
            currentClientSocket?.close()
            currentServerSocket?.close()
            currentClientSocket = null
            currentServerSocket = null
            _isConnected.update { false }
        }
    }

    override fun release() {
        if (isFoundDeviceReceiverRegistered) {
            try {
                context.unregisterReceiver(foundDeviceReceiver)
                isFoundDeviceReceiverRegistered = false
            } catch (e: Exception) {
                Log.e("Bluetooth", "Exception caught: ${e.message}")
            }

        }

        if (isBluetoothStateReceiverRegistered) {
            try {
                context.unregisterReceiver(bluetoothStateReceiver)
                isBluetoothStateReceiverRegistered = false
            } catch (e: Exception) {
                Log.e("Bluetooth", "Exception caught: ${e.message}")
            }

        }

        closeConnection()
    }

    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return
        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val SERVICE_UUID = "0b6a013a-01b8-4b6a-9a1c-1bea99419b71"
    }
}
