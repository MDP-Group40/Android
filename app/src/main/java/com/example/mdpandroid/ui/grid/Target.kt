package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun Target(
    cellSize: Int
){
    Box(
        modifier = Modifier
            .size(cellSize.dp)
            .border(2.dp, Color.Blue, CircleShape)
            .background(Color.Black )

    )
}