package com.example.mdpandroid.ui.sidebar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SidebarButton(
    text: String,
    handleSingleClick: () -> Unit,
    handleDoubleClick: () -> Unit = {}, // Default no-op for double click
    maxWidth: Dp = 140.dp, // Default maxWidth for the button
    backgroundColor: Color = Color.Black // Add a parameter for background color
) {
    Box(
        modifier = Modifier
            .widthIn(maxWidth)
            .heightIn(max = 55.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { handleSingleClick() }, // Single tap handler
                    onDoubleTap = { handleDoubleClick() } // Double tap handler
                )
            }
            .background(backgroundColor) // Add the background color here
            .border(BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface))
            .padding(2.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center // Center the content in the Box
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center, // Center the text inside the box
            lineHeight = 13.sp,
            minLines = 2,
            maxLines = 2
        )
    }
}


