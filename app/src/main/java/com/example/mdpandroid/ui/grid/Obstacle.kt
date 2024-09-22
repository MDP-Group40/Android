package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mdpandroid.R
import com.example.mdpandroid.data.model.Facing

@Composable
fun Obstacle(
    cellSize: Int,
    targetID: Int,
    numberOnObstacle: Int?,
    initialFacing: Facing?,  // Accept initial facing
    isEditing: Boolean = false,
    onFacingChange: (Facing?) -> Unit  // Callback to update the underlying model
) {
    // Use MutableState in the UI to track the facing direction
    var facingState by remember { mutableStateOf(initialFacing) }

    // Call this function whenever the facing changes to update the data model
    fun updateFacing(newFacing: Facing?) {
        facingState = newFacing
        onFacingChange(newFacing)
    }

    // Modify the size if the obstacle is being edited (enlarged)
    val sizeModifier = if (isEditing) {
        Modifier.size((cellSize * 1.5).dp) // Increase size when editing
    } else {
        Modifier.size(cellSize.dp)
    }

    // Add pointerInput to detect drag gestures to change facing direction
    val gestureModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            // Detect drag direction and update facing state accordingly
            val (dx, dy) = dragAmount
            val newFacing = when {
                dx > 0 -> Facing.EAST  // Dragging right
                dx < 0 -> Facing.WEST  // Dragging left
                dy > 0 -> Facing.SOUTH // Dragging down
                dy < 0 -> Facing.NORTH // Dragging up
                else -> facingState
            }
            updateFacing(newFacing)
        }
    }

    // Add the gestureModifier to sizeModifier
    val combinedModifier = sizeModifier.then(gestureModifier)

    val borderModifier = Modifier.drawBehind {
        val strokeWidth = 5.dp.toPx() // Convert the dp value to pixels for border width
        val color = Color.Yellow

        // Draw the border according to the facing direction
        when (facingState) {
            Facing.NORTH -> {
                drawLine(
                    color = color,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            }
            Facing.SOUTH -> {
                drawLine(
                    color = color,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
            Facing.EAST -> {
                drawLine(
                    color = color,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
            Facing.WEST -> {
                drawLine(
                    color = color,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = strokeWidth
                )
            }
            null -> Unit // Handle the null case if needed
        }
    }

    Box(
        modifier = combinedModifier.then(borderModifier), // Apply size and border modifiers
        contentAlignment = Alignment.Center // Ensures content is centered
    ) {
        val painter: Painter = painterResource(id = R.drawable.item)

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // Display number or targetID
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
}




