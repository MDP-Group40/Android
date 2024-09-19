package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mdpandroid.ui.sidebar.SidebarViewModel

@Composable
fun GridMap(viewModel: SidebarViewModel, gridSize: Int, cellSize: Int) {
    Column(
        modifier = Modifier
            .padding(6.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (y in 0 until gridSize) {
            Row {
                for (x in 0 until gridSize) {
                    val obstacle = viewModel.getObstacleAt(x.toFloat(), y.toFloat())

                    GridCell(
                        cellSize = cellSize,
                        isObstacle = viewModel.isObstaclePosition(x.toFloat(), y.toFloat()),
                        isTarget = viewModel.isTargetPosition(x.toFloat(), y.toFloat()),
                        onClick = {
                            // Only allow adding/removing in "AddingObstacle" mode
                            if (viewModel.isAddingObstacle) {
                                if (viewModel.isObstaclePosition(x.toFloat(), y.toFloat())) {
                                    viewModel.removeObstacle(x.toFloat(), y.toFloat())
                                } else {
                                    viewModel.addObstacle(x.toFloat(), y.toFloat())
                                }
                            }
                            else if (viewModel.isAddingTarget){
                                if (viewModel.isTargetPosition(x.toFloat(), y.toFloat())) {
                                    viewModel.removeTarget(x.toFloat(), y.toFloat())
                                } else {
                                    viewModel.addTarget(x.toFloat(), y.toFloat())
                                }
                            }
                        },
                        onLongPress = {
                            // Trigger enlargement on long press
                            if (!viewModel.isAddingTarget && !viewModel.isAddingObstacle) {
                                viewModel.startEditingObstacleFacing(x.toFloat(), y.toFloat())
                            }
                        },
                        onDrag = { dx, dy ->
                            // Only change facing direction when not adding target
                            if (!viewModel.isAddingTarget && !viewModel.isAddingObstacle) {
                                viewModel.updateObstacleFacing(x.toFloat(), y.toFloat(), dx, dy)
                            }
                        },
                        numberOnObstacle = obstacle?.numberOnObstacle,
                        facing = obstacle?.facing,
                        targetID = obstacle?.targetID,
                        isEditing = viewModel.isEditingObstacle(x.toFloat(), y.toFloat()) // Check if the obstacle is being edited
                    )
                }
            }
        }
    }
}

