package com.example.mdpandroid.ui.buttons

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.mdpandroid.data.model.Modes
import com.example.mdpandroid.domain.BluetoothMessage
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
    // Function to handle the "Start" button click event
    fun handleStartClick() {
        val currentMode = sharedViewModel.mode.value

        // Check if the current mode is not IDLE
        if (currentMode != Modes.IDLE) {

            // Extract car, obstacles, and targets from SharedViewModel
            val car = sharedViewModel.car.value
            if (car == null) {
                // Show snackbar if no car is available
                sharedViewModel.showSnackbar("No car found. Set car position first.", SnackbarDuration.Short)
                return
            }

            val obstacles = sharedViewModel.obstacles.toList()
            val targets = sharedViewModel.target.toList()

            // Create a StartMessage with the extracted data
            val startMessage = BluetoothMessage.StartMessage(
                car = car,
                obstacles = obstacles,
                target = targets,
                mode = currentMode,
                senderName = "Android Device", // Modify as needed
                isFromLocalUser = true
            )

            // Serialize StartMessage to JSON
            val startMessageJson = Json.encodeToString(startMessage)

            // Log the StartMessage in JSON form
            Log.d("StartMessage", "StartMessage JSON: $startMessageJson")

            try {
                bluetoothViewModel.sendMessage(startMessage)
                navController.safeNavigate("start")
            } catch (e: Exception) {
                Log.e("StartMessage", "Failed to send StartMessage", e)
                sharedViewModel.showSnackbar("Failed to send message.", SnackbarDuration.Short)
            }
        } else {
            // Show snackbar if mode is IDLE
            sharedViewModel.showSnackbar("Please select a mode", SnackbarDuration.Short)
        }
    }

    // Layout for the buttons
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { navController.safeNavigate("bluetooth") }) {
            Text(text = "Menu")
        }

        Spacer(modifier = Modifier.width(8.dp)) // Added more padding between buttons

        Button(onClick = { handleStartClick() }) {
            Text(text = "Start")
        }
    }
}
