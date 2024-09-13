package com.example.mdpandroid.ui.bluetooth.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mdpandroid.domain.BluetoothMessage
import com.example.mdpandroid.ui.theme.MDPAndroidTheme

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {
    val messageShape = RoundedCornerShape(
        topStart = if (message.isFromLocalUser) 15.dp else 0.dp,
        topEnd = 15.dp,
        bottomStart = 15.dp,
        bottomEnd = if (message.isFromLocalUser) 0.dp else 15.dp
    )

    Column(
        modifier = modifier
            .clip(messageShape)
            .border(2.dp, Color.White, shape = messageShape) // Add the white rounded border
            .padding(16.dp)
    ) {
        Text(
            text = message.senderName,
            fontSize = 15.sp,
            color = Color.White
        )
        Text(
            text = message.message,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.widthIn(max = 250.dp)
        )
    }
}


@Preview
@Composable
fun ChatMessagePreview() {
    MDPAndroidTheme {
        ChatMessage(
            message = BluetoothMessage(
                message = "Hello World! I am creating my android application",
                senderName = "Pixel 6",
                isFromLocalUser = false
            )
        )
    }
}