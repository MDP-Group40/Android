package com.example.mdpandroid.ui.bluetooth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdpandroid.data.domain.BluetoothController
import kotlinx.coroutines.flow.*

class BluetoothViewModel(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _uiState = MutableStateFlow(BluetoothUiState())
    val uiState: StateFlow<BluetoothUiState> = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _uiState
    ) { scannedDevices, pairedDevices, uiState ->
        uiState.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BluetoothUiState()
    )

    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }
}
