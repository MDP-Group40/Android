package com.example.mdpandroid.ui.simulator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mdpandroid.data.model.GameControlMode
import com.example.mdpandroid.ui.buttons.GameControls
import com.example.mdpandroid.ui.car.Car
import com.example.mdpandroid.ui.car.CarViewModel
import com.example.mdpandroid.ui.car.CarViewModelFactory
import com.example.mdpandroid.ui.footer.InformationDisplay
import com.example.mdpandroid.ui.grid.DirectionSelectorViewModel
import com.example.mdpandroid.ui.grid.DirectionSelectorViewModelFactory
import com.example.mdpandroid.ui.grid.GridMap
import com.example.mdpandroid.ui.header.StatusDisplay
import com.example.mdpandroid.ui.shared.SharedViewModel
import com.example.mdpandroid.ui.sidebar.CoordinateEntryDialog
import com.example.mdpandroid.ui.sidebar.Sidebar
import com.example.mdpandroid.ui.sidebar.SidebarViewModel
import com.example.mdpandroid.ui.sidebar.SidebarViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun IdleScreen(
    sharedViewModel: SharedViewModel,
    carViewModel: CarViewModel = viewModel(factory = CarViewModelFactory(sharedViewModel)),
    sidebarViewModel: SidebarViewModel = viewModel(factory = SidebarViewModelFactory(sharedViewModel)),
    directionSelectorViewModel: DirectionSelectorViewModel = viewModel(factory = DirectionSelectorViewModelFactory(sidebarViewModel, sharedViewModel)),
    navController: NavHostController
) {
    val cellSize = 24
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Scaffold with SnackbarHost
    Scaffold(
        modifier = Modifier.background(Color.Black), // Set the background of the entire screen to black
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingview ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingview) // Apply the padding here
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            StatusDisplay(status = "IDLE")

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                Column(modifier = Modifier.weight(5f)) {
                    Box(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxSize()
                    ) {
                        // Draw the grid
                        GridMap(
                            viewModel = sidebarViewModel,
                            gridSize = sharedViewModel.gridSize,
                            cellSize = cellSize,
                            directionSelectorViewModel = directionSelectorViewModel
                        )
                        // Overlay the car on top of the grid
                        Car(
                            viewModel = sharedViewModel,
                            cellSize = cellSize
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Sidebar(
                        sidebarViewModel = sidebarViewModel,
                        sharedViewModel = sharedViewModel,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
            InformationDisplay(viewModel = sharedViewModel)
            // Control buttons
            GameControls(
                viewModel = if (sharedViewModel.gameControlMode.value === GameControlMode.DRIVING) carViewModel else directionSelectorViewModel,
                navController = navController,
                sharedViewModel =  sharedViewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
    }

    LaunchedEffect(sharedViewModel.snackbarMessage.value) {
        sharedViewModel.snackbarMessage.value?.let { message ->
            coroutineScope.launch {
                val duration = sharedViewModel.snackbarDuration.value ?: SnackbarDuration.Indefinite
                snackbarHostState.showSnackbar(message, duration = duration)
            }
        }
    }

    if (sidebarViewModel.dialogForTarget) CoordinateEntryDialog(
        viewModel = sidebarViewModel,
        isObstacle = false
    )

    if (sidebarViewModel.dialogForObstacle) CoordinateEntryDialog(
        viewModel = sidebarViewModel,
        isObstacle = true
    )
}





