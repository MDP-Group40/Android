// Sidebar.kt
package com.example.mdpandroid.ui.sidebar

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.mdpandroid.data.model.Modes
import com.example.mdpandroid.data.model.Orientation
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material3.TextFieldDefaults


@Composable
fun Sidebar(
    sidebarViewModel: SidebarViewModel,
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState
){
    Spacer(modifier = Modifier.height(12.dp))
    ResetButton(sharedViewModel)
    Spacer(modifier = Modifier.height(12.dp))
    SetTargetButton(sidebarViewModel, sharedViewModel, snackbarHostState)
    Spacer(modifier = Modifier.height(7.dp))
    SetObstacleButton(sidebarViewModel, sharedViewModel, snackbarHostState)
    Spacer(modifier = Modifier.height(7.dp))
    SetCarButton(sharedViewModel = sharedViewModel)
    Spacer(modifier = Modifier.height(12.dp))
    ImageRecognitionButton(viewModel = sidebarViewModel, sharedViewModel)
    Spacer(modifier = Modifier.height(7.dp))
    FastestPathButton(viewModel = sidebarViewModel, sharedViewModel)
}

@Composable
fun SetTargetButton(
    sidebarViewModel: SidebarViewModel,
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    // Handle single click behavior
    fun handleSingleClick() {
        // Toggle adding target and show/hide snackbar
        if (sidebarViewModel.isAddingObstacle) {
            coroutineScope.launch { snackbarHostState.currentSnackbarData?.dismiss() }
            sharedViewModel.resetSnackbar()
            sidebarViewModel.toggleAddingObstacle()
        }

        sidebarViewModel.toggleAddingTarget()

        if (sidebarViewModel.isAddingTarget) {
            sharedViewModel.showSnackbar("Click on the grid cells to add / remove the obstacle")
        } else {
            coroutineScope.launch { snackbarHostState.currentSnackbarData?.dismiss() }
            sharedViewModel.resetSnackbar()
        }
    }

    // Handle double click behavior
    fun handleDoubleClick() {
        sidebarViewModel.showCoordinateDialogForTarget()
    }

    // Use the updated SidebarButton that supports both single and double click
    SidebarButton(
        text = if (sidebarViewModel.isAddingTarget) "DONE" else "SET\nOBSTACLES",
        handleSingleClick = {
            handleSingleClick()
        },
        handleDoubleClick = {
            handleDoubleClick()
        },
        backgroundColor = if (sidebarViewModel.isAddingTarget) Color.DarkGray else Color.Black
    )
}

@Composable
fun SetObstacleButton(
    sidebarViewModel: SidebarViewModel,
    sharedViewModel: SharedViewModel,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    // Handle single click behavior
    fun handleSingleClick() {
        // Check if adding target is active, and disable it
        if (sidebarViewModel.isAddingTarget) {
            coroutineScope.launch { snackbarHostState.currentSnackbarData?.dismiss() }
            sharedViewModel.resetSnackbar()
            sidebarViewModel.toggleAddingTarget()
        }

        // Toggle adding obstacle and show/hide snackbar
        sidebarViewModel.toggleAddingObstacle()

        if (sidebarViewModel.isAddingObstacle) {
            sharedViewModel.showSnackbar("Click on the grid cells to add / remove the target")
        } else {
            coroutineScope.launch { snackbarHostState.currentSnackbarData?.dismiss() }
            sharedViewModel.resetSnackbar()
        }
    }

    // Handle double click behavior
    fun handleDoubleClick() {
        sidebarViewModel.showCoordinateDialogForObstacle()
    }

    // Use the updated SidebarButton that supports both single and double click
    SidebarButton(
        text = if (sidebarViewModel.isAddingObstacle) "DONE" else "SET\nTARGETS",
        handleSingleClick = { handleSingleClick() },
        handleDoubleClick = { handleDoubleClick() },
        backgroundColor = if (sidebarViewModel.isAddingObstacle) Color.DarkGray else Color.Black
    )
}

@Composable
fun SetCarButton(sharedViewModel: SharedViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    SidebarButton(
        text = "SET\nPAC-MAN",
        handleSingleClick = { showDialog = true }
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
fun ImageRecognitionButton(viewModel: SidebarViewModel, sharedViewModel: SharedViewModel) {
    val backgroundColor = if (sharedViewModel.mode.value == Modes.IMAGERECOGNITION) Color.DarkGray else Color.Black

    SidebarButton(
        text = "IMAGE\nRECOGNITION",
        backgroundColor = backgroundColor,
        handleSingleClick = {
            viewModel.toggleMode(Modes.IMAGERECOGNITION)
            Log.d("ButtonAction", "Image Recognition Button Clicked")
        }
    )
}

@Composable
fun FastestPathButton(viewModel: SidebarViewModel, sharedViewModel: SharedViewModel) {
    val backgroundColor = if (sharedViewModel.mode.value == Modes.FASTESTPATH) Color.DarkGray else Color.Black

    SidebarButton(
        text = "FASTEST\nPATH",
        backgroundColor = backgroundColor,
        handleSingleClick = {
            viewModel.toggleMode(Modes.FASTESTPATH)
            Log.d("ButtonAction", "Fastest Path Button Clicked")
        }
    )
}

@Composable
fun ResetButton(sharedViewModel: SharedViewModel) {
    SidebarButton(
        text = "RESET\nSETTINGS",
        handleSingleClick = {
            // Reset the car, obstacles, targets, and mode
            sharedViewModel.resetCar()
            sharedViewModel.resetObstacles()
            sharedViewModel.resetTargets()
            sharedViewModel.resetMode()
            sharedViewModel.resetTargetId()

            // Show a snackbar for a short duration
            sharedViewModel.showSnackbar("Reset to default configuration", SnackbarDuration.Short)

            Log.d("ButtonAction", "Reset Button Clicked: Defaults restored")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoordinateEntryDialog(viewModel: SidebarViewModel, isObstacle: Boolean) {
    var x by remember { mutableStateOf("") }
    var y by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()  // Create a coroutine scope for the composable

    Dialog(onDismissRequest = { viewModel.dismissCoordinateDialog() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )


        {
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
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,  // Set text color when focused
                    unfocusedTextColor = Color.Black,  // Set text color when unfocused
                    focusedLabelColor = Color.Black,  // Label color when focused
                    cursorColor = Color.Black,  // Cursor color
                    focusedIndicatorColor = Color.Black,  // Underline color when focused
                    unfocusedIndicatorColor = Color.Gray  // Underline color when unfocused
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
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,  // Set text color when focused
                    unfocusedTextColor = Color.Black,  // Set text color when unfocused
                    focusedLabelColor = Color.Black,  // Label color when focused
                    cursorColor = Color.Black,  // Cursor color
                    focusedIndicatorColor = Color.Black,  // Underline color when focused
                    unfocusedIndicatorColor = Color.Gray  // Underline color when unfocused
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
                coroutineScope.launch {
                    val xValue = x.toFloatOrNull()
                    val yValue = y.toFloatOrNull()

                    if (xValue != null && yValue != null && xValue in 0f..20f && yValue in 0f..20f) {
                        // Handle the add operation on the background thread
                        if (isObstacle) viewModel.addObstacle(xValue, yValue)
                        else viewModel.addTarget(xValue, yValue)

                        viewModel.dismissCoordinateDialog()
                    } else {
                        // Since errorMessage is tied to UI state, it needs to be updated on the main thread
                        withContext(Dispatchers.Main) {
                            errorMessage = "Coordinates must be numbers between 0 and 20"
                        }
                    }
                }
            }) {
                val message: String = if (isObstacle) "Add Target" else "Add Obstacle"

                Text(message)
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

    val coroutineScope = rememberCoroutineScope()

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
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,  // Set text color when focused
                        unfocusedTextColor = Color.Black,  // Set text color when unfocused
                        focusedLabelColor = Color.Black,  // Label color when focused
                        cursorColor = Color.Black,  // Cursor color
                        focusedIndicatorColor = Color.Black,  // Underline color when focused
                        unfocusedIndicatorColor = Color.Gray,  // Underline color when unfocused

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
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,  // Set text color when focused
                        unfocusedTextColor = Color.Black,  // Set text color when unfocused
                        focusedLabelColor = Color.Black,  // Label color when focused
                        cursorColor = Color.Black,  // Cursor color
                        focusedIndicatorColor = Color.Black,  // Underline color when focused
                        unfocusedIndicatorColor = Color.Gray  // Underline color when unfocused
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Orientation")
                Spacer(modifier = Modifier.height(4.dp))
                OrientationDropdown(
                    selectedOrientation = selectedOrientation,
                    onOrientationSelected = { selectedOrientation = it },
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
                        coroutineScope.launch {
                            val xValue = x.toFloatOrNull()
                            val yValue = y.toFloatOrNull()

                            val isValid = xValue != null && yValue != null && xValue in 0f..20f && yValue in 0f..20f

                            if (isValid) {
                                onConfirm(xValue!!, yValue!!, selectedOrientation)
                                onDismissRequest()
                            } else {
                                errorMessage = "Invalid coordinates. Please enter numbers between 0 and 20."
                            }
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
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color.Black
                    )
                )
            }
        }
    }
}
