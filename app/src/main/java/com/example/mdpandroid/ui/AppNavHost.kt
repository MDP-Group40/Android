package com.example.mdpandroid.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mdpandroid.ui.bluetooth.BluetoothScreen
import com.example.mdpandroid.ui.simulator.GridScreen

@Composable
fun AppNavHost(navController: NavHostController, bluetoothHandler: BluetoothHandler) {
    NavHost(navController = navController, startDestination = "grid") {
        composable("grid") {
            GridScreen(navController= navController)
        }
        composable("bluetooth") {
            BluetoothScreen(bluetoothHandler = bluetoothHandler)
        }
    }
}
