package com.example.mdpandroid.ui.bluetooth

import com.example.mdpandroid.domain.BluetoothDevice
import com.example.mdpandroid.domain.BluetoothDeviceDomain
import com.example.mdpandroid.domain.BluetoothMessage


data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList(),
    val message: String? = null, // Add a message field for UI
    val connectingDevice: BluetoothDevice? = null,  // Device being connected
    val connectedDevice: BluetoothDevice? = null,    // Device that is connected
    val failedDevice: BluetoothDeviceDomain? = null
)