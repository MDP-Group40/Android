package com.example.mdpandroid.ui.buttons

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mdpandroid.R
import com.example.mdpandroid.domain.BluetoothMessage
import com.example.mdpandroid.ui.bluetooth.BluetoothViewModel
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun ABButton(viewModel: ControlViewModel,
             activeButton: String,
             setActiveButton: (String) -> Unit,
             bluetoothViewModel: BluetoothViewModel = hiltViewModel(),
             sharedViewModel: SharedViewModel){

    fun signalMovement(direction:String){
        if (sharedViewModel.drivingMode.value){  // Check if drivingMode is true
            val message = sharedViewModel.car.value?.let {
                BluetoothMessage.MovementMessage(
                    car = it,
                    direction = direction,
                    senderName = "Android Device",
                    isFromLocalUser = true
                )
            }

            // Serialize the message to JSON
            val jsonMessage = Json.encodeToString(message)
            Log.d("ABButton", "Sending Movement Message: $jsonMessage")

            if (message != null) {
                bluetoothViewModel.sendMessage(message)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        MoveButton(
            onPress = {
                viewModel.handleButtonA()
                signalMovement("FORWARD")
            },
            onRelease = {
                viewModel.handleStopMovement()
                signalMovement("STOP")
            },
            label = "A",
            activeButton = activeButton,
            setActiveButton = { setActiveButton(it) },
            modifier = Modifier
                .size(120.dp)
                .offset(x = 40.dp),
            imageResId = R.drawable.a_button
        )

        Spacer(modifier = Modifier.height(20.dp))
        MoveButton(
            onPress = {
                viewModel.handleButtonB()
                signalMovement("BACKWARD")
            },
            onRelease = {
                viewModel.handleStopMovement()
                signalMovement("STOP")
            },
            label = "B",
            activeButton = activeButton,
            setActiveButton = { setActiveButton(it) },
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-30).dp),
            imageResId = R.drawable.b_button
        )
    }
}
