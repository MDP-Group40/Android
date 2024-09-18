package com.example.mdpandroid.ui.bluetooth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mdpandroid.ui.bluetooth.BluetoothUiState

@Composable
fun UpdatesList(
    state: BluetoothUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Black),  // Set the background to black as per the image
        horizontalAlignment = Alignment.Start
    ) {
        // Header Text
        BasicText(
            text = "UPDATES:",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,   // Adjust the size to match the header style
                textAlign = androidx.compose.ui.text.style.TextAlign.Left
            )
        )

        // Wrapping LazyColumn in a Box to constrain height and enable scrolling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(213.dp)  // Set a fixed height for scrolling; you can adjust this
        ) {
            // List of messages
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)  // Space between each message
            ) {
                items(state.messages) { message ->
                    BasicText(
                        text = message.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontSize = 20.sp,  // Adjust size to match the message text
                            textAlign = androidx.compose.ui.text.style.TextAlign.Left
                        )
                    )
                }
            }
        }
    }
}
