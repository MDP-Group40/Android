package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GridCell() {
    val color = Color.LightGray

    Box(
        modifier = Modifier
            .size(15.dp)
            .background(color)
            .border(1.dp, Color.Black)
    )
}
