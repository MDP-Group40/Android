package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.mdpandroid.ui.simulator.SidebarViewModel

@Composable
fun GridMap(viewModel: SidebarViewModel, gridSize: Int, cellSize: Int) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (y in 0 until gridSize) {
            Row {
                for (x in 0 until gridSize) {
                    val obstacle = viewModel.getObstacleAt(x.toFloat(), y.toFloat())
                    val targetID = obstacle?.targetID

                    GridCell(
                        x = x,
                        y = y,
                        cellSize = cellSize,
                        isObstacle = viewModel.isObstaclePosition(x.toFloat(), y.toFloat()),
                        onClick = {
                            if (viewModel.isAddingObstacle) {
                                if (viewModel.isObstaclePosition(x.toFloat(), y.toFloat())) {
                                    viewModel.removeObstacle(x.toFloat(), y.toFloat())
                                } else {
                                    viewModel.addObstacle(x.toFloat(), y.toFloat())
                                }
                            }
                        },
                        onDrag = {
                            if (viewModel.isAddingObstacle) {
                                if (viewModel.isObstaclePosition(x.toFloat(), y.toFloat())) {
                                    viewModel.addObstacle(x.toFloat(), y.toFloat())
                                }
                            }
                        },
                        targetID = targetID // Pass the target ID to the GridCell
                    )
                }
            }
        }
    }
}
