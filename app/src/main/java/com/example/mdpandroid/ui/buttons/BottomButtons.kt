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
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.height

@Composable
fun ClickEffectBox(
    onClick: () -> Unit,
    imageResource: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var clicked by remember { mutableStateOf(false) }

    // Add a slight animation for the effect
    val scale by animateFloatAsState(if (clicked) 0.9f else 1f, label = "") // Shrinks the button slightly

    // LaunchedEffect to handle reset after click
    LaunchedEffect(clicked) {
        if (clicked) {
            delay(100) // Simulate a delay for the click effect
            clicked = false // Reset clicked state after delay
        }
    }

    // Box that handles the click
    Box(
        modifier = modifier
            .scale(scale) // Applies the scaling effect
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        clicked = true
                        onClick()
                    }
                )
            }
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = contentDescription,
            modifier = Modifier
                .background(Color.Transparent)
        )
    }
}

@Composable
fun BottomButtons(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    bluetoothViewModel: BluetoothViewModel = hiltViewModel() // Inject BluetoothViewModel via Hilt
) {
    // Function to handle single click
    fun handleSingleClick() {
        Log.d("UI", "handleSingleClick called")
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
    }

    // Function to handle double click
    fun handleDoubleClick() {
        Log.d("UI", "handleDoubleClick called")
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // For your first button
        ClickEffectBox(
            onClick = { navController.safeNavigate("bluetooth") },
            imageResource = R.drawable.menu_text_button,
            contentDescription = "Menu Button"
        )
        Spacer(modifier = Modifier.width(12.dp))

        // For your second button
        ClickEffectBox(
            onClick = { handleSingleClick() },
            imageResource = R.drawable.select_text_button,
            contentDescription = "Start Button"
        )
        Spacer(modifier = Modifier.width(12.dp))

        // For your third button
        ClickEffectBox(
            onClick = { handleDoubleClick() },
            imageResource = R.drawable.start_text_button,
            contentDescription = "Select Button"
        )
    }
}
