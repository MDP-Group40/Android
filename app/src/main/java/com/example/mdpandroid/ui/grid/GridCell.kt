package com.example.mdpandroid.ui.grid

import android.util.Log
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
import com.example.mdpandroid.data.model.Facing

@Composable
fun GridCell(
    cellSize: Int,
    isObstacle: Boolean,
    isTarget: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
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
                    onLongPress = {
                        Log.d("GridCell", "Long press detected at obstacle: $targetID")
                        onLongPress()  // Call the onLongPress that triggers the ViewModel state change
                    }
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
                onFacingChange = onFacingChange
            )
        } else if (isTarget) {
            Target(cellSize = cellSize)
        }
    }
}

