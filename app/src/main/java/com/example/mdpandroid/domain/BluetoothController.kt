package com.example.mdpandroid.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val isConnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>
    val errors: SharedFlow<String>
    
    val lastConnectedDevice: BluetoothDeviceDomain?

    fun startDiscovery()
    fun stopDiscovery()

    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDevice): Flow<ConnectionResult>
    fun listenForIncomingMessages(): Flow<ConnectionResult>

    suspend fun reconnectToLastDevice(retries: Int = 3): ConnectionResult
    suspend fun trySendMessage(bluetoothMessage: BluetoothMessage): BluetoothMessage?

    fun closeConnection()
    fun release()

    fun enableBluetooth()
    fun disableBluetooth()

    fun isBluetoothEnabled(): Boolean
}