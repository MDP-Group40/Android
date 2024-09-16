package com.example.mdpandroid.ui.bluetooth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mdpandroid.ui.bluetooth.components.ChatScreen
import com.example.mdpandroid.ui.bluetooth.components.ScanningScreen
import com.example.mdpandroid.ui.buttons.GameControls
import com.example.mdpandroid.ui.car.CarViewModel
import com.example.mdpandroid.ui.car.CarViewModelFactory
import com.example.mdpandroid.ui.shared.SharedViewModel

@Composable
fun ConnectingTab(
    navController: NavHostController,
    viewModel: BluetoothViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    carViewModel: CarViewModel = viewModel(
        factory = CarViewModelFactory(sharedViewModel)
    ),) {
    val state by viewModel.state.collectAsState()
    val isBluetoothOn by viewModel.isBluetoothEnabled.collectAsState() // Bluetooth ON/OFF state
    val isScanning by viewModel.isScanning.collectAsState() // Scanning state

    Surface(
        modifier = Modifier
            .fillMaxSize()  // Ensure the Surface takes up the entire screen
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ScanningScreen becomes scrollable
            Box(
                modifier = Modifier
                    .weight(1f)  // Take up most of the space
            ) {
                ScanningScreen(
                    state = state,
                    isBluetoothOn = isBluetoothOn,
                    isScanning = isScanning,
                    onToggleBluetooth = { viewModel.toggleBluetooth() },
                    onStartScan = { viewModel.startScan() },
                    onStopScan = { viewModel.stopScan() },
                    onDeviceClick = { device -> viewModel.connectToDevice(device) },
                    modifier = Modifier.verticalScroll(rememberScrollState()) // Make it scrollable
                )
            }

            // GameControls will have a fixed height
            GameControls(
                viewModel = carViewModel,
                navController = navController,
                sharedViewModel = sharedViewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)  // Set a fixed height for GameControls
            )
        }
    }


}

@Composable
fun MessagesTab(navController: NavHostController, viewModel: BluetoothViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        // Always show ChatScreen, and pass connection status
        ChatScreen(
            state = state,
            onDisconnect = {
                viewModel.disconnectFromDevice()
                navController.navigate("grid") // Navigate back to connect screen on disconnect
            },
            onSendMessage = { textMessage ->
                viewModel.sendMessage(textMessage) // Send the text message using the updated function
            },
            isConnected = state.isConnected, // Pass connection status to control message input
            navController = navController
        )
    }
}




