package com.example.mdpandroid.ui.bluetooth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mdpandroid.domain.BluetoothMessage
import com.example.mdpandroid.ui.bluetooth.BluetoothUiState
import com.example.mdpandroid.ui.header.StatusDisplay
import com.example.mdpandroid.ui.safeNavigate
import com.example.mdpandroid.ui.sidebar.SidebarButton

@Composable
fun ChatScreen(
    state: BluetoothUiState,
    navController: NavHostController,
    onDisconnect: () -> Unit,
    onSendMessage: (String) -> Unit,
    isConnected: Boolean
) {
    val message = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)  // Set the background color to black
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        // StatusDisplay to show the "MESSAGES" header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusDisplay(status = "MESSAGES")
            IconButton(onClick = onDisconnect) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Message list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add a system message if not connected
            if (!isConnected) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ChatMessage(
                            message = BluetoothMessage(
                                message = "Please connect to a device first",
                                senderName = "System",
                                isFromLocalUser = false
                            )
                        )
                        Spacer(modifier = Modifier.width(15.dp))


                        Column{
                            Spacer(modifier = Modifier.height(45.dp))
                            // Add SidebarButton to navigate to Bluetooth screen
                            SidebarButton(
                                text = "Go to Bluetooth",
                                handleSingleClick = {
                                    navController.safeNavigate("bluetooth")
                                }
                            )
                        }
                    }
                }
            }

            // Display all existing messages
            items(state.messages) { message ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ChatMessage(
                        message = message,
                        modifier = Modifier.align(
                            if (message.isFromLocalUser) Alignment.End else Alignment.Start
                        )
                    )
                }
            }
        }

        // Input section for typing and sending a message
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message.value,
                onValueChange = { newValue -> message.value = newValue },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "Message...", color = Color.Black)
                },
                enabled = isConnected // Disable the text field if not connected
            )
            IconButton(
                onClick = {
                    if (isConnected) {
                        onSendMessage(message.value)
                        message.value = ""
                        keyboardController?.hide()
                    }
                },
                enabled = isConnected // Disable send button if not connected
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message"
                )
            }
        }
    }
}

