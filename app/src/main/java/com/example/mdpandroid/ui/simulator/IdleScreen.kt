package com.example.mdpandroid.ui.simulator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mdpandroid.R
import com.example.mdpandroid.ui.buttons.GameControls
import com.example.mdpandroid.ui.car.Car
import com.example.mdpandroid.ui.car.CarViewModel
import com.example.mdpandroid.ui.car.CarViewModelFactory
import com.example.mdpandroid.ui.grid.GridMap
import com.example.mdpandroid.ui.header.StatusDisplay
import com.example.mdpandroid.ui.safeNavigate
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
    navController: NavHostController
) {
    val gridSize = 20
    val cellSize = 24
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Scaffold with SnackbarHost
    Scaffold(
        modifier = Modifier.background(Color.Black), // Set the background of the entire screen to black
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingview -> // Apply the paddingview parameter
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
                        GridMap(sidebarViewModel, gridSize, cellSize)
                        // Overlay the car on top of the grid
                        Car(sharedViewModel, cellSize)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 120.dp)
                    .zIndex(1f)
            ) {
                LeftRightTab(navController = navController)
            }
            // Control buttons
            GameControls(
                carViewModel, navController, Modifier
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


@Composable
fun LeftRightTab(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Left Button (L)
        Button(
            onClick = { navController.safeNavigate("grid") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent // Make button background transparent
            ),
            modifier = Modifier.size(230.dp), // Adjust size for the button
            content = {
                Image(
                    painter = painterResource(id = R.drawable.l_button), // Your image resource
                    contentDescription = "L Button",
                    modifier = Modifier.fillMaxSize() // Make sure image fills button space
                )
            }
        )

        // Right Button (R)
        Button(
            onClick = { navController.safeNavigate("message") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.size(230.dp), // Ensure consistent size for the button
            content = {
                Image(
                    painter = painterResource(id = R.drawable.r_button), // Your image resource
                    contentDescription = "R Button",
                    modifier = Modifier.fillMaxSize() // Make sure image fills button space
                )
            }
        )
    }
}





