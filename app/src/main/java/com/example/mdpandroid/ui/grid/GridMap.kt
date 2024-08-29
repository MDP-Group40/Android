package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.mdpandroid.ui.simulator.SimulatorViewModel

@Composable
fun GridMap(viewModel: SimulatorViewModel) {
    val gridSize = 20
    val density = LocalDensity.current
    var gridOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                // Capture the position of the grid in screen coordinates
                gridOffset = coordinates.localToWindow(Offset.Zero)
                viewModel.updateGridOffset(gridOffset)
                println("Grid offset updated to: $gridOffset")
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        println("Drag started at screen offset: $offset")
                        viewModel.startDragging(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        println("Dragging to screen offset: ${change.position}")
                        viewModel.updateDragging(change.position)
                    },
                    onDragEnd = {
                        viewModel.placeObstacleOnGrid(viewModel.dragPosition.value, gridOffset, density)
                        viewModel.endDragging()
                    }
                )
            }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (y in 0 until gridSize) {
                Row {
                    for (x in 0 until gridSize) {
                        val isObstacle = viewModel.isObstaclePosition(x.toFloat(), y.toFloat())
                        if (isObstacle) {
                            ObstacleCell()
                        } else {
                            GridCell()
                        }
                    }
                }
            }
        }
    }
}
