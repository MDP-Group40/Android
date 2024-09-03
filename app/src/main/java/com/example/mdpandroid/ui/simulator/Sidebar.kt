// Sidebar.kt
package com.example.mdpandroid.ui.simulator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import com.example.mdpandroid.ui.shared.SharedViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.mdpandroid.data.model.Orientation
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Box


@Composable
fun SidebarButton(
    text: String,
    onClick: () -> Unit,
    maxWidth: Dp = 120.dp // Default maxWidth for the button
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .widthIn(maxWidth), // Set a max width for the button
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center, // Center the text inside the button
            minLines = 2, // Allow the text to wrap into multiple lines
            maxLines =2
        )
    }
}
@Composable
fun ResetButton(onClick: () -> Unit) {
    SidebarButton(text = "RESET", onClick = onClick)
}

@Composable
fun SetObstacleButton(
    sidebarViewModel: SidebarViewModel,
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState // Pass the SnackbarHostState here
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var clickCount by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    fun handleClick() {
        val currentTime = System.currentTimeMillis()
        clickCount++

        if (clickCount == 2 && currentTime - lastClickTime < 300) {
            sidebarViewModel.showCoordinateDialog()
            clickCount = 0
        } else {
            lastClickTime = currentTime
            sidebarViewModel.setPendingSingleClick(currentTime)
        }
    }

    val pendingSingleClick by sidebarViewModel.pendingSingleClick.collectAsState()

    LaunchedEffect(pendingSingleClick) {
        if (pendingSingleClick != 0L && clickCount == 1) {
            delay(300)
            if (clickCount == 1) {
                sidebarViewModel.toggleAddingObstacle()
                clickCount = 0

                if (sidebarViewModel.isAddingObstacle) {
                    sharedViewModel.showSnackbar("Click on the grid cells to add / remove the obstacle")
                } else {
                    // Dismiss the snackbar immediately
                    coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                    sharedViewModel.resetSnackbar()
                }
            }
        }
    }

    val buttonText = if (sidebarViewModel.isAddingObstacle) "DONE" else "SET OBSTACLES"
    SidebarButton(text = buttonText, onClick = { handleClick() })
}

@Composable
fun SetCarButton(sharedViewModel: SharedViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    SidebarButton(
        text = "SET PAC-MAN",
        onClick = { showDialog = true }
    )

    if (showDialog) {
        SetCarDialog(
            onDismissRequest = { showDialog = false },
            onConfirm = { x, y, orientation ->
                sharedViewModel.setCar(positionX = x, positionY = y, orientation = orientation)
            }
        )
    }
}

@Composable
fun FastestPathButton(onClick: () -> Unit) {
    SidebarButton(text = "FASTEST PATH", onClick = onClick)
}

@Composable
fun ImageRecognitionButton(onClick: () -> Unit) {
    SidebarButton(text = "IMAGE RECOGNITION", onClick = onClick)
}

@Composable
fun CoordinateEntryDialog(viewModel: SidebarViewModel) {
    var x by remember { mutableStateOf("") }
    var y by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { viewModel.dismissCoordinateDialog() }) {
        Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

        ) {
            Text("Enter X and Y coordinates")

            TextField(
                value = x,
                onValueChange = {
                    x = it
                    errorMessage = ""
                },
                label = { Text("X") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )
            TextField(
                value = y,
                onValueChange = {
                    y = it
                    errorMessage = ""
                },
                label = { Text("Y") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val xValue = x.toFloatOrNull()
                val yValue = y.toFloatOrNull()

                if (xValue != null && yValue != null && xValue in 0f..20f && yValue in 0f..20f) {
                    viewModel.addObstacle(xValue, yValue)
                    viewModel.dismissCoordinateDialog()
                } else {
                    errorMessage = "Coordinates must be numbers between 0 and 20"
                }
            }) {
                Text("Add Obstacle")
            }
        }
    }
}

@Composable
fun SetCarDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Float, Float, Orientation) -> Unit
) {
    var x by remember { mutableStateOf("") }
    var y by remember { mutableStateOf("") }
    var selectedOrientation by remember { mutableStateOf(Orientation.NORTH) }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = Color.Black,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Set Car Position and Orientation")

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = x,
                    onValueChange = {
                        x = it
                        errorMessage = ""
                    },
                    label = { Text("X Coordinate") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = y,
                    onValueChange = {
                        y = it
                        errorMessage = ""
                    },
                    label = { Text("Y Coordinate") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown for selecting orientation
                Text("Orientation")
                Spacer(modifier = Modifier.height(4.dp))
                OrientationDropdown(
                    selectedOrientation = selectedOrientation,
                    onOrientationSelected = { selectedOrientation = it }
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val xValue = x.toFloatOrNull()
                        val yValue = y.toFloatOrNull()

                        if (xValue != null && yValue != null && xValue in 0f..20f && yValue in 0f..20f) {
                            onConfirm(xValue, yValue, selectedOrientation)
                            onDismissRequest()
                        } else {
                            errorMessage = "Invalid coordinates. Please enter numbers between 0 and 20."
                        }
                    }) {
                        Text("Set Car")
                    }
                }
            }
        }
    }
}


@Composable
fun OrientationDropdown(
    selectedOrientation: Orientation,
    onOrientationSelected: (Orientation) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val orientations = Orientation.entries.toTypedArray()

    Box {
        // This is the TextButton that shows the selected orientation and triggers the dropdown
        TextButton(
            onClick = { expanded = true }
        ) {
            Text(text = selectedOrientation.name) // Pass the `text` parameter here
        }

        // Dropdown menu that shows all orientation options
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            orientations.forEach { orientation ->
                DropdownMenuItem(
                    text = { Text(orientation.name) },  // Use `text` parameter correctly
                    onClick = {
                        onOrientationSelected(orientation)
                        expanded = false
                    }
                )
            }
        }
    }
}
