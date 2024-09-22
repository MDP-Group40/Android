package com.example.mdpandroid.ui.footer



import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun DisplayStyle(
    text: String,
    backgroundColor: Color = Color.Black
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center // Center the content in the Box
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center, // Center the text inside the box
            lineHeight = 13.sp,
            minLines = 2,
            maxLines = 2
        )
    }
}


