package com.example.mdpandroid.ui.bluetooth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mdpandroid.ui.BluetoothHandler

@Composable
fun BluetoothScreen(bluetoothHandler: BluetoothHandler) {

    val bluetoothController = bluetoothHandler.getBluetoothController()
    val bluetoothViewModel: BluetoothViewModel = viewModel(factory = BluetoothViewModelFactory(bluetoothController))
    val uiState by bluetoothViewModel.uiState.collectAsState()

    ScanScreen(
        state = uiState,
        onStartScan = { bluetoothViewModel.startScan() },
        onStopScan = { bluetoothViewModel.stopScan() }
    )

}
