package com.example.mdpandroid.ui.simulator

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mdpandroid.data.model.Modes
import com.example.mdpandroid.ui.car.Car
import com.example.mdpandroid.ui.grid.GridMap
import com.example.mdpandroid.ui.header.StatusDisplay
import com.example.mdpandroid.ui.safeNavigate
import com.example.mdpandroid.ui.shared.SharedViewModel
import com.example.mdpandroid.ui.sidebar.SidebarViewModel
import com.example.mdpandroid.ui.sidebar.SidebarViewModelFactory

@Composable
fun RunningScreen(
    sharedViewModel: SharedViewModel,
    sidebarViewModel: SidebarViewModel = viewModel(factory = SidebarViewModelFactory(sharedViewModel)),
    navController: NavHostController
) {
    val gridSize = 20
    val cellSize = 29
    var header = ""

    if (sharedViewModel.mode.value == Modes.IMAGERECOGNITION) header = "IMAGE RECOGNITION"
    else header = "FASTEST PATH"


    // Scaffold with SnackbarHost

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusDisplay(status = header)
            IconButton(onClick = { navController.safeNavigate("grid") }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Row(modifier = Modifier
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
        }
    }
}



