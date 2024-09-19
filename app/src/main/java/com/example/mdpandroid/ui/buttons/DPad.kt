package com.example.mdpandroid.ui.buttons

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mdpandroid.R
import com.example.mdpandroid.domain.BluetoothMessage
import com.example.mdpandroid.domain.MovementMessage
import com.example.mdpandroid.ui.bluetooth.BluetoothViewModel
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun DPad(
    viewModel: ControlViewModel,
    activeButton: String,
    setActiveButton: (String) -> Unit,
    bluetoothViewModel: BluetoothViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel
) {
    fun signalMovement(direction:String){
        if (sharedViewModel.drivingMode.value){  // Check if drivingMode is true
            val message = sharedViewModel.car.value?.let {
                MovementMessage(
                    car = it,
                    direction = direction,
                    senderName = "Android Device",
                    isFromLocalUser = true
                )
            }

            // Serialize the message to JSON
            val jsonMessage = Json.encodeToString(message)
            Log.d("DPADS", "Sending Movement Message: $jsonMessage")

            if (message != null) {
                bluetoothViewModel.sendMessage(message)
            }
        }
    }
    Box(
        modifier = Modifier
            .size(200.dp) // Adjust size as per your layout needs
    ) {
        // Background image for the DPad
        Image(
            painter = painterResource(id = R.drawable.pad_bg),
            contentDescription = "DPad Background",
            modifier = Modifier
                .fillMaxSize()
                .scale(1f)
                .offset(x = (-15).dp),
        )

        // UP button (forward)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoveButton(
                onPress = {
                    viewModel.handleButtonUp()
                    signalMovement("FORWARD")
                },
                onRelease = {
                    viewModel.handleStopMovement()
                    signalMovement("STOP")
                },
                label = "^",
                activeButton = activeButton,
                setActiveButton = { setActiveButton(it) },
                modifier = Modifier
                    .size(75.dp)
                    .offset(x = 47.dp, y = (-5).dp),
                imageResId = R.drawable.up,
            )
        }

        // LEFT and RIGHT buttons
        Row {
            MoveButton(
                onPress = {
                    viewModel.handleButtonLeft()
                    signalMovement("LEFT")
                },
                onRelease = {
                    viewModel.handleStopMovement()
                    signalMovement("STOP")
                },
                label = "<",
                activeButton = activeButton,
                setActiveButton = { setActiveButton(it) },
                modifier = Modifier
                    .size(75.dp)
                    .offset(x = (-10).dp, y = 54.dp),
                imageResId = R.drawable.left
            )

            MoveButton(
                onPress = {
                    viewModel.handleButtonRight()
                    signalMovement("RIGHT")
                },
                onRelease = {
                    viewModel.handleStopMovement()
                    signalMovement("STOP")
                },
                label = ">",
                activeButton = activeButton,
                setActiveButton = { setActiveButton(it) },
                modifier = Modifier
                    .size(75.dp)
                    .offset(x = 30.dp, y = 54.dp),
                imageResId = R.drawable.right
            )
        }

        // DOWN button (backward)
        MoveButton(
            onPress = {
                viewModel.handleButtonDown()
                signalMovement("BACKWARD")
            },
            onRelease = {
                viewModel.handleStopMovement()
                signalMovement("STOP")
            },
            label = "_",
            activeButton = activeButton,
            setActiveButton = { setActiveButton(it) },
            modifier = Modifier
                .size(75.dp)
                .offset(x = 45.dp, y = 110.dp),
            imageResId = R.drawable.down
        )
    }
}