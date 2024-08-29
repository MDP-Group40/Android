package com.example.mdpandroid.ui.buttons

import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.example.mdpandroid.ui.simulator.SimulatorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ControlButtons(viewModel: SimulatorViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        HoldButton(
            onHold = { viewModel.onMoveLeft() },
            onRelease = { viewModel.onStopMove() },
            label = "Turn Left"
        )
        Spacer(modifier = Modifier.width(16.dp))
        HoldButton(
            onHold = { viewModel.onMoveForward() },
            onRelease = { viewModel.onStopMove() },
            label = "Move Forward",
            forwardBackward = true
        )
        Spacer(modifier = Modifier.width(16.dp))
        HoldButton(
            onHold = { viewModel.onMoveRight() },
            onRelease = { viewModel.onStopMove() },
            label = "Turn Right"
        )
        Spacer(modifier = Modifier.width(16.dp))
        HoldButton(
            onHold = { viewModel.onMoveBackward() },
            onRelease = { viewModel.onStopMove() },
            label = "Move Backward",
            forwardBackward = true
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HoldButton(
    onHold: () -> Unit,
    onRelease: () -> Unit,
    label: String,
    forwardBackward: Boolean = false
) {
    var isContinuous by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        modifier = Modifier.pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    val currentTime = System.currentTimeMillis()
                    if ((currentTime - lastClickTime < 200) &&  forwardBackward) { // Double click detected
                        isContinuous = !isContinuous
                        if (isContinuous ) {
                            onHold()
                            coroutineScope.launch {
                                while (isContinuous ) {
                                    onHold()
                                    delay(100L)
                                }
                            }
                        } else {
                            onRelease() // Stop continuous movement on single click
                        }
                    } else if (isContinuous) {
                        isContinuous = false
                        onRelease() // Stop continuous movement on single click
                    } else {
                        coroutineScope.launch {
                            onHold()
                        }
                    }
                    lastClickTime = currentTime
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (!isContinuous) {
                        onRelease()
                    }
                    true
                }
                else -> false
            }
        },
        onClick = {}
    ) {
        BasicText(text = label)
    }
}
