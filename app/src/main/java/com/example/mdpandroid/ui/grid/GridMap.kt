package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mdpandroid.ui.sidebar.SidebarViewModel

@Composable
fun GridMap(
    viewModel: SidebarViewModel,
    gridSize: Int,
    cellSize: Int,
    directionSelectorViewModel: DirectionSelectorViewModel
) {

    Column(
        modifier = Modifier
            .padding(6.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (y in 0 until gridSize) {

            val transformedY = gridSize - y

            Row {
                for (x in 0 until gridSize) {
                    val obstacle = viewModel.getObstacleAt(x.toFloat(), transformedY.toFloat())

                    GridCell(
                        x = x.toFloat(),
                        y = transformedY.toFloat(),
                        cellSize = cellSize,
                        isObstacle = viewModel.isObstaclePosition(x.toFloat(), transformedY.toFloat()),
                        isTarget = viewModel.isTargetPosition(x.toFloat(), transformedY.toFloat()),
                        onClick = {
                            // Only allow adding/removing in "AddingObstacle" or "AddingTarget" mode
                            if (viewModel.isAddingObstacle) {
                                if (viewModel.isObstaclePosition(x.toFloat(), transformedY.toFloat())) {
                                    viewModel.removeObstacle(x.toFloat(), transformedY.toFloat())
                                } else {
                                    viewModel.addObstacle(x.toFloat(), transformedY.toFloat())
                                }
                            } else if (viewModel.isAddingTarget) {
                                if (viewModel.isTargetPosition(x.toFloat(), transformedY.toFloat())) {
                                    viewModel.removeTarget(x.toFloat(), transformedY.toFloat())
                                } else {
                                    viewModel.addTarget(x.toFloat(), transformedY.toFloat())
                                }
                            } else if (!viewModel.isAddingTarget && !viewModel.isAddingObstacle && (viewModel.getObstacleAt(x.toFloat(), transformedY.toFloat()) != null)) {
                                // Trigger enlargement on long press
                                directionSelectorViewModel.startEditingObstacleFacing(
                                    x.toFloat(),
                                    transformedY.toFloat()
                                )
                            }
                        },
                        numberOnObstacle = obstacle?.numberOnObstacle,
                        facing = obstacle?.facing,
                        targetID = obstacle?.targetID,
                        isEditing = directionSelectorViewModel.isEditingObstacle(x.toFloat(), transformedY.toFloat()),
                        onFacingChange = { newFacing ->
                            // Call a simplified method that only takes the new facing value.
                            viewModel.updateObstacleFacingWithFacing(x.toFloat(), transformedY.toFloat(), newFacing)
                        },
                        directionSelectorViewModel = directionSelectorViewModel
                    )
                }
            }
        }
    }
}

