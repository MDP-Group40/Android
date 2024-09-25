package com.example.mdpandroid.ui.buttons

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.mdpandroid.R
import com.example.mdpandroid.data.model.Modes
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
            val startMessage = StartMessage(
                car = car,
                obstacles = obstacles,
                target = targets,
                mode = if(currentMode == Modes.IMAGERECOGNITION) 0 else 1,
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .offset(y = (-20).dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.safeNavigate("bluetooth") },
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                content = {
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center // Align content to the center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tiny_button), // Your image resource
                            contentDescription = "Menu Button",
                            modifier = Modifier
                                .background(Color.Transparent)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp)) // Added more padding between buttons

            Button(
                onClick = { handleStartClick() },
                shape = CircleShape,
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                content = {
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center // Align content to the center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tiny_button), // Your image resource
                            contentDescription = "Menu Button",
                            modifier = Modifier
                                .background(Color.Transparent)
                        )
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y=(-15).dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.text_menu),
                modifier = Modifier,
                contentDescription = "Menu_Text"
            )

            Spacer(modifier = Modifier.width(30.dp)) // Padding between text images

           /* Image(
                painter = painterResource(id = R.drawable.text_start),
                modifier = Modifier,
                contentDescription = "Start_Text"
            )*/
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center // Center the content in the Box
            ) {
                Text(
                    text = "Start",
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
    }
}
