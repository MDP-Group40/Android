package com.example.mdpandroid.ui.bluetooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mdpandroid.data.domain.BluetoothController

class BluetoothViewModelFactory(
    private val bluetoothController: BluetoothController
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {
            return BluetoothViewModel(bluetoothController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
