package com.example.mdpandroid.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mdpandroid.ui.simulator.IdleScreen
import com.example.mdpandroid.ui.bluetooth.ConnectingTab
import com.example.mdpandroid.ui.bluetooth.MessagesTab
import com.example.mdpandroid.ui.shared.SharedViewModel
import com.example.mdpandroid.ui.simulator.RunningScreen

@Composable
fun AppNavHost(navController: NavHostController) {

    val sharedViewModel: SharedViewModel = viewModel()  // Ensures the same instance is used

    NavHost(navController = navController, startDestination = "grid") {
        composable("grid") {
            IdleScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
        composable("bluetooth") {
            ConnectingTab(navController = navController, sharedViewModel = sharedViewModel) // Navigate to connect tab
        }
        composable("message") {
            MessagesTab(navController)  // Navigate to message tab
        }
        composable("start") {
            RunningScreen(sharedViewModel = sharedViewModel, navController = navController) // Navigate to message tab
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