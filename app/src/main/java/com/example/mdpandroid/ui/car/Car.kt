package com.example.mdpandroid.ui.car

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
        val offsetX = ((car.x - 0.2) * cell.value).dp
        val offsetY = ((car.transformY - 1.6)* cell.value).dp

        Box(

            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(cell * car.width, cell * car.height)
                .graphicsLayer(rotationZ = car.rotationAngle) // Use rotationAngle directly
                .background(Color.Black)

        ) {
            //taking the png image from res/drawable
            val painter: Painter = painterResource(id = R.drawable.pac_man)

            // Display the image
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )

        }
    }
}