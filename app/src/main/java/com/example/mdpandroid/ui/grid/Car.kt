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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.mdpandroid.R
import com.example.mdpandroid.ui.shared.SharedViewModel



@Composable
fun Car(viewModel: SharedViewModel, cellSize: Int) {
    // Observe car state
    val carPosition by viewModel.car

    // Only render if the car is not null
    carPosition?.let { car ->
        val cell = cellSize.dp

        // Calculate the offset in Dp using density
        val offsetX = ((car.positionX - 1) * cell.value).dp
        val offsetY = ((car.positionY - 1.5) * cell.value).dp

        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(cell * car.width, cell * car.height)
                .graphicsLayer(rotationZ = car.rotationAngle) // Use rotationAngle directly
                .background(Color.Black)
        ) {
            //taking the png image from res/drawable
            val painter: Painter = painterResource(id = R.drawable.car)

            // Display the image
            Image(
                painter = painter,
                contentDescription = null, // Provide content description for accessibility if needed
                modifier = Modifier.fillMaxSize()
            )
            /*Canvas(modifier = Modifier.fillMaxSize()) {
                drawCarFrontIndicator(size)
            }*/
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
