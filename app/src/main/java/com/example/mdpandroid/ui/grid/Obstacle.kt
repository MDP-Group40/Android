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
    viewModel: DirectionSelectorViewModel,
    x: Float,  // Add x and y to identify the obstacle
    y: Float,
    onFacingChange: (Facing?) -> Unit
) {
    var facingState by remember { mutableStateOf(initialFacing) }

    // Update facing state and call the provided callback
    fun updateFacing(newFacing: Facing?) {
        facingState = newFacing
        onFacingChange(newFacing)
    }

    // Define the border based on the current facing direction
    val borderModifier = Modifier.drawBehind {
        val strokeWidth = 8.dp.toPx()
        val color = Color(0xFF0001FD)

        when (facingState) {
            Facing.NORTH -> drawLine(color, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth)
            Facing.SOUTH -> drawLine(color, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth)
            Facing.EAST -> drawLine(color, Offset(size.width, 0f), Offset(size.width, size.height), strokeWidth)
            Facing.WEST -> drawLine(color, Offset(0f, 0f), Offset(0f, size.height), strokeWidth)
            null -> Unit
        }
    }

    Box(
        modifier = Modifier
            .size(cellSize.dp)
            .background(if (isEditing) Color.Red else Color.Black)
            .then(borderModifier),
        contentAlignment = Alignment.Center
    ) {
        val painter: Painter = painterResource(id = R.drawable.item)
        Image(painter = painter, contentDescription = null, modifier = Modifier.fillMaxSize())

        if (numberOnObstacle == null){
            BasicText(
                text = targetID.toString(),
                style = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Center),
                modifier = Modifier
            )
        }
        else{
            BasicText(
                    text = numberOnObstacle.toString(),
                    style = TextStyle(color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                    modifier = Modifier

                )
        }

    }

    // Listen for changes in the ViewModel and update facing state
    LaunchedEffect(viewModel.currentFacing) {
        viewModel.currentFacing?.let { newFacing ->
            if (viewModel.isEditingObstacle(x, y)) {  // Ensure we're updating the correct obstacle
                updateFacing(newFacing)
            }
        }
    }
}



