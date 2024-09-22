package com.example.mdpandroid.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mdpandroid.ui.bluetooth.BluetoothViewModel
import com.example.mdpandroid.ui.bluetooth.ConnectingTab
import com.example.mdpandroid.ui.bluetooth.MessagesTab
import com.example.mdpandroid.ui.shared.SharedViewModel
import com.example.mdpandroid.ui.simulator.IdleScreen
import com.example.mdpandroid.ui.simulator.RunningScreen

@Composable
fun AppNavHost(navController: NavHostController) {

    val sharedViewModel: SharedViewModel = hiltViewModel()  // Ensures the same instance is used
    val bluetoothViewModel: BluetoothViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "grid") {
        composable("grid") {
            sharedViewModel.resetSnackbar()
            IdleScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
        composable("bluetooth") {
            sharedViewModel.resetSnackbar()
            ConnectingTab(navController = navController, sharedViewModel = sharedViewModel, viewModel = bluetoothViewModel) // Navigate to connect tab
        }
        composable("message") {
            sharedViewModel.resetSnackbar()
            MessagesTab(navController = navController, viewModel = bluetoothViewModel)  // Navigate to message tab
        }
        composable("start") {
            sharedViewModel.resetSnackbar()
            RunningScreen(sharedViewModel = sharedViewModel, navController = navController, viewModel = bluetoothViewModel) // Navigate to message tab
        }
    }
}


fun NavHostController.safeNavigate(route: String) {
    // Check if the current destination is the same as the target route
    if (this.currentDestination?.route != route) {
        this.navigate(route) {
            restoreState = true
            launchSingleTop = true // Prevent multiple copies of the same destination in the back stack
        }
    }
}