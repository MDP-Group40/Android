package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.example.mdpandroid.R
import com.example.mdpandroid.data.model.Facing

@Composable
fun Obstacle(
    cellSize: Int,
    targetID: Int,
    numberOnObstacle: Int?,
    initialFacing: Facing?,
    isEditing: Boolean = false,
    viewModel: DirectionSelectorViewModel,  // ViewModel to manage facing selection
    onFacingChange: (Facing?) -> Unit  // Callback to update the underlying model
) {
    // Track the current facing state
    var facingState by remember { mutableStateOf(initialFacing) }

    // Update the facing when the ViewModel's state changes
    fun updateFacing(newFacing: Facing?) {
        facingState = newFacing
        onFacingChange(newFacing)
    }

    // Define the border based on the current facing direction
    val borderModifier = Modifier.drawBehind {
        val strokeWidth = 8.dp.toPx() // Convert dp to pixels for the border width
        val color = Color.Yellow

        // Draw the border based on the current facing state
        when (facingState) {
            Facing.NORTH -> drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth
            )
            Facing.SOUTH -> drawLine(
                color = color,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth
            )
            Facing.EAST -> drawLine(
                color = color,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth
            )
            Facing.WEST -> drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = strokeWidth
            )
            null -> Unit
        }
    }

    // Render the obstacle box
    Box(
        modifier = Modifier
            .size(cellSize.dp)
            .background(if (isEditing) Color.Red else Color.Black) // Highlight background when editing
            .then(borderModifier),  // Apply the border modifier
        contentAlignment = Alignment.Center
    ) {
        // Render the obstacle image
        val painter: Painter = painterResource(id = R.drawable.item)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // Display either the targetID or numberOnObstacle
        if (numberOnObstacle == null) {
            BasicText(
                text = "$targetID",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 9.dp)
            )
        } else {
            BasicText(
                text = "$numberOnObstacle",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 9.dp)
            )
        }
    }

    // Observe the currentFacing from the ViewModel and update the facing state when it changes
    LaunchedEffect(viewModel.currentFacing) {
        viewModel.currentFacing?.let { newFacing ->
            updateFacing(newFacing)  // Update the local state and trigger recomposition
        }
    }
}


