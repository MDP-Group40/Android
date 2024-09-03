package com.example.mdpandroid.ui.buttons

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mdpandroid.ui.simulator.CarViewModel

@Composable
fun GameControls(viewModel: CarViewModel, navController: NavHostController) {

    Column(modifier = Modifier
        .background(Color(0xFF04A9FC))
        .height(400.dp)
        .fillMaxWidth()
        .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left D-Pad for turning
            DPad(viewModel)

            // Right A and B buttons for moving forward/backward
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                MoveButton(
                    onPress = { viewModel.onMoveForward() },
                    onRelease = { viewModel.onStopMove() },
                    label = "A",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(25.dp))
                MoveButton(
                    onPress = { viewModel.onMoveBackward() },
                    onRelease = { viewModel.onStopMove() },
                    label = "B",
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        Button(onClick = { navController.navigate("bluetooth") }) {
            Text(text = "Menu")
        }
    }
}

@Composable
fun DPad(viewModel: CarViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MoveButton(
            onPress = { viewModel.onMoveForward() }, // Keep forward movement in D-Pad
            onRelease = { viewModel.onStopMove() },
            label = "^",
            modifier = Modifier.size(60.dp).padding(bottom = 10.dp)
        )

        Row {
            MoveButton(
                onPress = { viewModel.onMoveLeft() },
                onRelease = { viewModel.onStopMove() },
                label = "<",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            MoveButton(
                onPress = { viewModel.onMoveRight() },
                onRelease = { viewModel.onStopMove() },
                label = ">",
                modifier = Modifier.size(60.dp)
            )
        }

        MoveButton(
            onPress = { viewModel.onMoveBackward() }, // Keep backward movement in D-Pad
            onRelease = { viewModel.onStopMove() },
            label = "_",
            modifier = Modifier.size(60.dp).padding(top = 10.dp)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MoveButton(
    onPress: () -> Unit,
    onRelease: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    Button(
        onClick = { onPress() },
        modifier = modifier.pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isPressed) {
                        isPressed = true
                        onPress()
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isPressed) {
                        isPressed = false
                        onRelease()
                    }
                    true
                }
                else -> false
            }
        },
    ) {
        BasicText(text = label)
    }
}
