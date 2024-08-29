package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.mdpandroid.ui.simulator.SimulatorViewModel
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas

@Composable
fun Car(viewModel: SimulatorViewModel) {
    // Observe car state
    val carPosition by viewModel.car
    val cellSize = 15.dp

    // Get the density for converting dp to pixels
    val density = LocalDensity.current

    // Calculate the offset in Dp using density
    val offsetX = with(density) { ((carPosition.positionX - 1) * cellSize.value).dp }
    val offsetY = with(density) { ((carPosition.positionY - 1.5) * cellSize.value).dp }

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(cellSize * carPosition.width, cellSize * carPosition.height)
            .graphicsLayer(rotationZ = carPosition.rotationAngle) // Use rotationAngle directly
            .background(Color.Blue)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCarFrontIndicator(size)
        }
    }
}

private fun DrawScope.drawCarFrontIndicator(carSize: Size) {
    // Determine the size of the front indicator (e.g., 1/3 of the car width)
    val indicatorSize = carSize.width / 3

    // Path for the triangle at the front of the car
    val path = Path().apply {
        moveTo(carSize.width / 2, 0f) // Start at the top center (assuming the car's front is "up")
        lineTo(carSize.width / 2 + indicatorSize, indicatorSize) // Bottom right corner of the triangle
        lineTo(carSize.width / 2 - indicatorSize, indicatorSize) // Bottom left corner of the triangle
        close()
    }

    // Draw the triangle with a different color to indicate the front
    drawPath(path, color = Color.White)
}
