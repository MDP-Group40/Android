package com.example.mdpandroid.domain

typealias BluetoothDeviceDomain = BluetoothDevice

data class BluetoothDevice(
    val name: String?, // The name of the device
    val address: String // MAC address
)