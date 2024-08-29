// Sidebar.kt

package com.example.mdpandroid.ui.simulator

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.mdpandroid.ui.grid.ObstacleCell

@Composable
fun ObstacleSidebar(viewModel: SimulatorViewModel) {

    Column(modifier = Modifier.background(Color.LightGray).padding(8.dp)) {
        Text("Obstacles")

        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            viewModel.startDragging(offset)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            viewModel.updateDragging(change.position)
                        },
                        onDragEnd = {
                            // The gridMap will handle the end drag placement
                        }
                    )
                }
        ) {
            ObstacleCell()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.showCoordinateDialog()
        }) {
            Text("Enter Coordinates")
        }
    }
}

@Composable
fun CoordinateEntryDialog(viewModel: SimulatorViewModel) {
    var x by remember { mutableStateOf("") }
    var y by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { viewModel.dismissCoordinateDialog() }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text("Enter X and Y coordinates")

            TextField(
                value = x,
                onValueChange = {
                    x = it
                    errorMessage = ""
                },
                label = { Text("X") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            TextField(
                value = y,
                onValueChange = {
                    y = it
                    errorMessage = ""
                },
                label = { Text("Y") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val xValue = x.toFloatOrNull()
                val yValue = y.toFloatOrNull()

                if (xValue != null && yValue != null && xValue in 0f..20f && yValue in 0f..20f) {
                    viewModel.addObstacle(xValue, yValue)
                    viewModel.dismissCoordinateDialog()
                } else {
                    errorMessage = "Coordinates must be numbers between 0 and 20"
                }
            }) {
                Text("Add Obstacle")
            }
        }
    }
}

