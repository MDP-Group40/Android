package com.example.mdpandroid.ui.grid

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    x : Float,
    y : Float,
    cellSize: Int,
    isObstacle: Boolean,
    isTarget: Boolean,
    onClick: () -> Unit,
    numberOnObstacle: Int?,
    facing: Facing?,
    targetID: Int? = null,
    isEditing: Boolean,
    onFacingChange: (Facing?) -> Unit,
    directionSelectorViewModel: DirectionSelectorViewModel
) {
    val painter = painterResource(id = R.drawable.gridcell)

    Box(
        modifier = Modifier
            .size(cellSize.dp)
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
        )
        if (isObstacle && targetID != null) {
            // Pass onDrag to Obstacle
            Obstacle(
                cellSize = cellSize,
                targetID = targetID,
                numberOnObstacle = numberOnObstacle,
                initialFacing = facing,
                isEditing = isEditing,
                viewModel = directionSelectorViewModel,
                onFacingChange = onFacingChange,
                x = x,
                y = y
            )
        } else if (isTarget) {
            Target(cellSize = cellSize)
        }
    }
}

