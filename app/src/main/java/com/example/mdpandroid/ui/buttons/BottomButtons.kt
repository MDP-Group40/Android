package com.example.mdpandroid.ui.buttons

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.mdpandroid.R
import com.example.mdpandroid.data.model.Modes
import com.example.mdpandroid.domain.BeginMessage
import com.example.mdpandroid.domain.StartMessage
import com.example.mdpandroid.ui.bluetooth.BluetoothViewModel
import com.example.mdpandroid.ui.safeNavigate
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun BottomButtons(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    bluetoothViewModel: BluetoothViewModel = hiltViewModel() // Inject BluetoothViewModel via Hilt
) {
    // Function to handle single click
    fun handleSingleClick() {
        val currentMode = sharedViewModel.mode.value
        val car = sharedViewModel.car.value

        if (car == null) {
            sharedViewModel.showSnackbar(
                "No car found. Set car position first.",
                SnackbarDuration.Short
            )
            return
        }

        val obstacles = sharedViewModel.obstacles.toList()
        val targets = sharedViewModel.target.toList()

        val startMessage = StartMessage(
            car = car,
            obstacles = obstacles,
            target = targets,
            mode = if (currentMode == Modes.IMAGERECOGNITION) 0 else 1,
            senderName = "Android Device",
            isFromLocalUser = true
        )

        val startMessageJson = Json.encodeToString(startMessage)
        Log.d("StartMessage", "StartMessage JSON: $startMessageJson")

        try {
            bluetoothViewModel.sendMessage(startMessage)
        } catch (e: Exception) {
            Log.e("StartMessage", "Failed to send StartMessage", e)
            sharedViewModel.showSnackbar("Failed to send message.", SnackbarDuration.Short)
        }
    }

    // Function to handle double click
    fun handleDoubleClick() {
        val currentMode = sharedViewModel.mode.value
        if (currentMode != Modes.IDLE) {
            val car = sharedViewModel.car.value
            if (car == null) {
                sharedViewModel.showSnackbar(
                    "No car found. Set car position first.",
                    SnackbarDuration.Short
                )
                return
            }

            val obstacles = sharedViewModel.obstacles.toList()
            val targets = sharedViewModel.target.toList()

            val startMessage = BeginMessage(
                car = car,
                obstacles = obstacles,
                target = targets,
                mode = if (currentMode == Modes.IMAGERECOGNITION) 0 else 1,
                senderName = "Android Device",
                isFromLocalUser = true
            )

            val startMessageJson = Json.encodeToString(startMessage)
            Log.d("StartMessage", "StartMessage JSON: $startMessageJson")

            try {
                bluetoothViewModel.sendMessage(startMessage)
                navController.safeNavigate("start")
            } catch (e: Exception) {
                Log.e("StartMessage", "Failed to send StartMessage", e)
                sharedViewModel.showSnackbar("Failed to send message.", SnackbarDuration.Short)
            }
        } else {
            sharedViewModel.showSnackbar("Please select a mode", SnackbarDuration.Short)
        }
    }

    // Layout for the buttons
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // First Button: Navigate to Bluetooth

        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { navController.safeNavigate("bluetooth") }
                    )
                }
                .background(Color.Transparent),
            contentAlignment = Alignment.Center // Align content to the center

        ) {
            Image(
                painter = painterResource(id = R.drawable.menu_text_button), // Your image resource
                contentDescription = "Menu Button",
                modifier = Modifier
                    .background(Color.Transparent)
            )
        }


        Spacer(modifier = Modifier.width(15.dp)) // Added padding between buttons

        // Second Button: Handle single and double tap

        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { handleSingleClick() },   // Single tap detection
                        onDoubleTap = { handleDoubleClick() } // Double tap detection
                    )
                }
                .background(Color.Transparent),
            contentAlignment = Alignment.Center // Align content to the center
        ) {
            Image(
                painter = painterResource(id = R.drawable.start_text_button), // Your image resource
                contentDescription = "Menu Button",
                modifier = Modifier
                    .background(Color.Transparent)
            )
        }
    }
}



