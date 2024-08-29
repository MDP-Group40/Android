package com.example.mdpandroid.ui.simulator

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mdpandroid.ui.buttons.ControlButtons
import com.example.mdpandroid.ui.grid.Car
import com.example.mdpandroid.ui.grid.GridMap

@Composable
fun GridScreen(viewModel: SimulatorViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.weight(3f)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Draw the grid
                    GridMap(viewModel)
                    // Overlay the car on top of the grid
                    Car(viewModel)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            ObstacleSidebar(viewModel)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Control buttons
        ControlButtons(viewModel)

        Spacer(modifier = Modifier.height(50.dp))
    }

    if (viewModel.showCoordinateDialog) {
        CoordinateEntryDialog(viewModel)
    }
}

