package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mdpandroid.R
import com.example.mdpandroid.data.model.Facing

@Composable
fun GridCell(
    cellSize: Int,
    isObstacle: Boolean,
    isTarget: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    numberOnObstacle: Int?,
    facing: Facing?,
    targetID: Int? = null,
    isEditing: Boolean,
    onFacingChange: (Facing?) -> Unit // Callback to propagate facing changes
) {
    val painter = painterResource(id = R.drawable.gridcell)

    Box(
        modifier = Modifier
            .size(cellSize.dp)
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongPress() }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    onDrag(dragAmount.x, dragAmount.y)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
        )
        if (isObstacle && targetID != null) {
            Obstacle(
                cellSize = cellSize,
                targetID = targetID,
                numberOnObstacle = numberOnObstacle,
                initialFacing = facing, // Pass the initial facing direction
                isEditing = isEditing,
                onFacingChange = onFacingChange // Handle facing changes
            )
        } else if (isTarget) {
            Target(cellSize = cellSize)
        }
    }
}


