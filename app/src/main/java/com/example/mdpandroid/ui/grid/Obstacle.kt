package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.example.mdpandroid.R
import com.example.mdpandroid.data.model.Facing
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset

@Composable
fun Obstacle(
    cellSize: Int,
    targetID: Int,
    numberOnObstacle: Int?,
    facing: MutableState<Facing?>?,
    isEditing: Boolean = false  // Control enlargement based on editing state
){
    // Modify the size if the obstacle is being edited (enlarged)
    val sizeModifier = if (isEditing) {
        Modifier.size((cellSize * 1.5).dp) // Increase size when editing
    } else {
        Modifier.size(cellSize.dp)
    }

    val borderModifier = Modifier.drawBehind {
        val strokeWidth = 5.dp.toPx() // Convert the dp value to pixels for border width
        val color = Color.Yellow

        if (facing != null) {
            when (facing.value) {
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
                null -> {
                    // No border if facing is null
                }
            }
        }
    }


    Box(
        modifier = sizeModifier.then(borderModifier), // Apply size and border modifiers
        contentAlignment = Alignment.Center // Ensures content is centered
    ) {
        val painter: Painter = painterResource(id = R.drawable.item)

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // Display number or targetID
        if (numberOnObstacle == null){
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

