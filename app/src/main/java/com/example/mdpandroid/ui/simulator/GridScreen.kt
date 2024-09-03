package com.example.mdpandroid.ui.simulator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mdpandroid.ui.buttons.GameControls
import com.example.mdpandroid.ui.grid.Car
import com.example.mdpandroid.ui.grid.GridMap
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun GridScreen(
    sharedViewModel: SharedViewModel = viewModel(),
    simulatorViewModel: CarViewModel = viewModel(factory = CarViewModelFactory(sharedViewModel)),
    sidebarViewModel: SidebarViewModel = viewModel(factory = SidebarViewModelFactory(sharedViewModel)),
    navController: NavHostController
) {
    val gridSize = 20
    val cellSize = 23
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Scaffold with SnackbarHost
    Scaffold(
        modifier = Modifier.background(Color.Black), // Set the background of the entire screen to black
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier
                .weight(1f)
                .padding(8.dp)
            ) {

                Column(modifier = Modifier.weight(5f)) {
                    Box(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxSize()
                    ) {
                        // Draw the grid
                        GridMap(sidebarViewModel, gridSize, cellSize)
                        // Overlay the car on top of the grid
                        Car(sharedViewModel, cellSize)
                    }
                }
                Column(modifier = Modifier
                    .weight(1f)
                ) {
                    SetObstacleButton(sidebarViewModel, sharedViewModel, snackbarHostState)
                    Spacer(modifier = Modifier.height(6.dp))
                    SetCarButton(sharedViewModel = sharedViewModel)
                }
            }
            // Control buttons
            GameControls(simulatorViewModel, navController)
        }
    }

    // Observe snackbarMessage from SharedViewModel
    val snackbarMessage by sharedViewModel.snackbarMessage

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Indefinite)
            }
        }
    }

    if (sidebarViewModel.showCoordinateDialog) {
        CoordinateEntryDialog(sidebarViewModel)
    }
}



