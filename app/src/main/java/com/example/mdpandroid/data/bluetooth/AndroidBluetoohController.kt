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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context: Context
): BluetoothController {


    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private var dataTransferService: BluetoothDataTransferService? = null

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
            if(newDevice in devices) devices else devices + newDevice
        }
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
        // Check if the device is already paired
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
            _isConnected.update { isConnected }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Can't connect to a non-paired device.")
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

    // Enable Bluetooth
    override fun enableBluetooth() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Permission to connect to Bluetooth is not granted")
            }
            return
        }

        if (bluetoothAdapter?.isEnabled == false) {
            // Use an intent to request enabling Bluetooth
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
            // Open Bluetooth settings where the user can disable Bluetooth manually
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
            !hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                    Log.e("BluetoothDiscovery", "Failed to start discovery")
                    CoroutineScope(Dispatchers.IO).launch {
                        _errors.emit("Failed to start discovery")
                    }
                } else {
                    Log.i("BluetoothDiscovery", "Discovery started successfully")
                    // Optionally emit a success message if needed
                }
            } catch (e: Exception) {
                Log.e("BluetoothDiscovery", "Error during discovery: ${e.message}")
                e.printStackTrace()
                CoroutineScope(Dispatchers.IO).launch {
                    _errors.emit("Error during discovery: ${e.message}")
                }
            }

            updatePairedDevices()
        }
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        bluetoothAdapter?.cancelDiscovery()

        if (isFoundDeviceReceiverRegistered) {
            context.unregisterReceiver(foundDeviceReceiver)
            isFoundDeviceReceiverRegistered = false
        }
    }

    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            currentServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "MDP_android",
                UUID.fromString(SERVICE_UUID)
            )

            var shouldLoop = true
            while(shouldLoop) {
                currentClientSocket = try {
                    currentServerSocket?.accept()
                } catch(e: IOException) {
                    shouldLoop = false
                    null
                }
                emit(ConnectionResult.ConnectionEstablished)
                currentClientSocket?.let { it ->
                    currentServerSocket?.close()
                    val service = BluetoothDataTransferService(it)
                    dataTransferService = service

                    emitAll(
                        service
                            .listenForIncomingMessages()
                            .map {
                                ConnectionResult.TransferSucceeded(it)
                            }
                    )
                }
            }
        }.onCompletion {
            closeConnection()
        }.flowOn(Dispatchers.IO)
    }

    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            Log.d("Bluetooth", "Attempting to connect to device: ${device.address}")

            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                Log.e("Bluetooth", "No BLUETOOTH_CONNECT permission")
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            try {
                Log.d("Bluetooth", "Getting remote device...")
                currentClientSocket = bluetoothAdapter
                    ?.getRemoteDevice(device.address)
                    ?.createRfcommSocketToServiceRecord(
                        UUID.fromString(SERVICE_UUID)
                    )
            } catch (e: IllegalArgumentException) {
                Log.e("Bluetooth", "Invalid device address or UUID: ${e.message}")
                emit(ConnectionResult.Error("Invalid device address or UUID"))
                return@flow
            }

            stopDiscovery() // Stop discovery when connecting
            Log.d("Bluetooth", "Stopped Bluetooth discovery")

            currentClientSocket?.let { socket ->
                try {
                    Log.d("Bluetooth", "Attempting to connect to socket...")
                    socket.connect()
                    Log.d("Bluetooth", "Connection established")
                    emit(ConnectionResult.ConnectionEstablished)

                    BluetoothDataTransferService(socket).also { service ->
                        dataTransferService = service
                        Log.d("Bluetooth", "Listening for incoming messages...")
                        emitAll(
                            service.listenForIncomingMessages()
                                .map { ConnectionResult.TransferSucceeded(it) }
                        )
                    }
                } catch (e: IOException) {
                    Log.e("Bluetooth", "IOException during socket connection: ${e.message}")
                    try {
                        socket.close()
                    } catch (closeException: IOException) {
                        Log.e("Bluetooth", "Error closing the socket: ${closeException.message}")
                    }
                    currentClientSocket = null
                    emit(ConnectionResult.Error("Connection was interrupted: ${e.message}"))
                }
        } ?: run {
                Log.e("Bluetooth", "Client socket is null, failed to create socket")
                emit(ConnectionResult.Error("Failed to create client socket"))
            }
        }.onCompletion { cause ->
            if (cause != null) {
                Log.e("Bluetooth", "Connection flow completed with error: ${cause.message}")
            } else {
                Log.d("Bluetooth", "Connection flow completed successfully")
            }
            closeConnection()
            Log.d("Bluetooth", "Connection closed")
        }.flowOn(Dispatchers.IO)
    }

    // Updated trySendMessage function to handle different types of BluetoothMessage
    override suspend fun trySendMessage(bluetoothMessage: BluetoothMessage): BluetoothMessage? {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return null
        }

        if (dataTransferService == null) {
            return null
        }

        return withContext(Dispatchers.IO) { // Ensuring background thread
            // Use sendBluetoothMessage from BluetoothDataTransferService
            val isSent = dataTransferService?.sendBluetoothMessage(bluetoothMessage) ?: false

            if (isSent) {
                Log.e("BluetoothController", "Send Bluetooth message.\n $bluetoothMessage")
                return@withContext bluetoothMessage // Return the sent message if successful
            } else {
                Log.e("BluetoothController", "Failed to send Bluetooth message.")
                return@withContext null
            }
        }
    }

    override fun closeConnection() {
        currentClientSocket?.close()
        currentServerSocket?.close()
        currentClientSocket = null
        currentServerSocket = null
    }

    override fun release() {
        // Unregister foundDeviceReceiver
        if (isFoundDeviceReceiverRegistered) {
            try {
                context.unregisterReceiver(foundDeviceReceiver)
                isFoundDeviceReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                // Ignore if not registered
            }
        }

        // Unregister bluetoothStateReceiver
        if (isBluetoothStateReceiverRegistered) {
            try {
                context.unregisterReceiver(bluetoothStateReceiver)
                isBluetoothStateReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                // Ignore if not registered
            }
        }

        closeConnection()
    }

    private fun updatePairedDevices() {
        if(!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
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