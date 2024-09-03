package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Obstacle(
    cellSize: Int,
    targetID: Int
){
    Box(
        modifier = Modifier
            .size(cellSize.dp)
            .border(1.dp, Color.Black)
            .background(Color.Red ),
        contentAlignment = Alignment.Center // Ensures content is centered
    ) {
        BasicText(
            text = "$targetID",
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}