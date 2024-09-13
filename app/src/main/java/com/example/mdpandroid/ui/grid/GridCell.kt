package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.mdpandroid.R

@Composable
fun GridCell(
    cellSize: Int,
    isObstacle: Boolean,
    isTarget: Boolean,
    onClick: () -> Unit,
    targetID: Int? = null
) {
    // If there's an expensive or non-composable function (e.g., some complex calculation), use remember
    val painter = painterResource(id = R.drawable.gridcell)

    Box(
        modifier = Modifier
            .size(cellSize.dp)
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() })
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
        )
        if (isObstacle && targetID != null) {
            Obstacle(cellSize = cellSize, targetID = targetID)
        } else if (isTarget) {
            Target(cellSize = cellSize)
        }
    }
}
