package com.example.mdpandroid.ui.grid

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.mdpandroid.R

@Composable
fun GridCell(
    x: Int,
    y: Int,
    cellSize: Int,
    isObstacle: Boolean,
    onClick: () -> Unit,
    onDrag: () -> Unit,
    targetID: Int? = null
) {


    Box(
        modifier = Modifier
            .size(cellSize.dp)
            //.border(1.dp, Color.White)
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    onClick()
                })
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { _, _ ->
                        onDrag()
                    }
                )
            },
        contentAlignment = Alignment.Center // Ensures content is centered
    ) {
        //taking the png image from res/drawable
        val painter: Painter = painterResource(id = R.drawable.gridcell)

        // Display the image
        Image(
            painter = painter,
            contentDescription = null, // Provide content description for accessibility if needed
            modifier = Modifier
        )
        if (isObstacle && targetID != null) {
            Obstacle(cellSize = cellSize, targetID = targetID)
            Log.d("GridCell", "Obstacle at ($x, $y) with ID: $targetID")
        }
    }
}
