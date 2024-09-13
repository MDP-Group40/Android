package com.example.mdpandroid.ui.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mdpandroid.R


@Composable
fun Obstacle(
    cellSize: Int,
    targetID: Int
){
    Box(
        modifier = Modifier
            .size(cellSize.dp),
            //.border(1.dp, Color.Black)
            //.background(Color.Red ),
        contentAlignment = Alignment.Center // Ensures content is centered
    ) {
        //taking the png image from res/drawable
        val painter: Painter = painterResource(id = R.drawable.item)

        // Display the image
        Image(
            painter = painter,
            contentDescription = null, // Provide content description for accessibility if needed
            modifier = Modifier.fillMaxSize()
        )
        BasicText(
            text = "$targetID",
            style = TextStyle(
                color = Color.Black,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 9.dp)
        )

    }
}