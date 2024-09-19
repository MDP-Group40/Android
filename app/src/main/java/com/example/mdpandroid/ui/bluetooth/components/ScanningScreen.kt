package com.example.mdpandroid.ui.bluetooth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mdpandroid.domain.BluetoothDevice
import com.example.mdpandroid.ui.bluetooth.BluetoothUiState
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.material.icons.filled.Close

@Composable
fun ScanningScreen(
    state: BluetoothUiState,
    isBluetoothOn: Boolean,   // Pass whether Bluetooth is on
    isScanning: Boolean,      // Pass whether scanning is in progress
    onToggleBluetooth: () -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit,
    connectToLastDevice: ()-> Unit,
    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        // Bluetooth and Scan buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Bluetooth ON/OFF button
            CustomStyledButton(
                onClick = { onToggleBluetooth() },
                text = if (isBluetoothOn) "BLUETOOTH: ON" else "BLUETOOTH: OFF"
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Search/Scanning button
            CustomStyledButton(
                onClick = {
                    if (isScanning) {
                        onStopScan()
                    } else {
                        onStartScan()
                    }
                },
                text = if (isScanning) "SEARCHING" else "SEARCH"
            )

            Spacer(modifier = Modifier.width(8.dp))

            CustomStyledButton(
                onClick = {
                    connectToLastDevice()
                },
                text = "RECONNECT"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of paired and discovered devices
        BluetoothDeviceList(
            pairedDevices = state.pairedDevices,
            scannedDevices = state.scannedDevices,
            connectingDevice = state.connectingDevice,
            connectedDevice = state.connectedDevice,
            failedDevice = state.failedDevice,  // Pass the failed device state
            onClick = onDeviceClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        )
    }
}


@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    connectingDevice: BluetoothDevice?,
    connectedDevice: BluetoothDevice?,
    failedDevice: BluetoothDevice?,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Paired Devices Section with Scrollable LazyColumn
        Text(
            text = "PAIRED DEVICES",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 100.dp)  // Set a max height of 200dp (adjust as needed)
        ) {
            LazyColumn {
                items(pairedDevices) { device ->
                    DeviceItem(
                        device = device,
                        isConnecting = device == connectingDevice,
                        isConnected = device == connectedDevice,
                        hasFailed = device == failedDevice,  // Pass failure state
                        onClick = onClick
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Discovered Devices Section with Scrollable LazyColumn
        Text(
            text = "DISCOVERED DEVICES",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)  // Set a max height of 200dp (adjust as needed)
        ) {
            LazyColumn {
                items(scannedDevices) { device ->
                    DeviceItem(
                        device = device,
                        isConnecting = device == connectingDevice,
                        isConnected = device == connectedDevice,
                        hasFailed = device == failedDevice,  // Pass failure state
                        onClick = onClick
                    )
                }
            }
        }
    }
}


@Composable
fun DeviceItem(
    device: BluetoothDevice,
    isConnecting: Boolean,
    isConnected: Boolean,
    hasFailed: Boolean,  // Add flag to indicate failure
    onClick: (BluetoothDevice) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(device) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the device name and address in a column
        Column(
            modifier = Modifier.weight(1f) // Ensure the text takes up available space
        ) {
            Text(
                text = device.name ?: "(No name)",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontFamily = FontFamily.Monospace)
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontFamily = FontFamily.Monospace)
            )
        }

        // Display a circular progress, checkmark, or cross depending on the connection state
        when {
            isConnecting -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp), // Size of the loading icon
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
            isConnected -> {
                Image(
                    imageVector = Icons.Default.Check,  // Use built-in checkmark icon
                    contentDescription = "Connected",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.Green) // Set icon color to green
                )
            }
            hasFailed -> {
                Image(
                    imageVector = Icons.Default.Close,  // Use built-in close icon for failure
                    contentDescription = "Connection Failed",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.Red)  // Set icon color to red
                )
            }
        }
    }
}


@Composable
fun CustomStyledButton(onClick: () -> Unit, text: String) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .border(2.dp, Color.White, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp)
            .height(40.dp)
            .width(180.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,  // Make the inside color black
            contentColor = Color.White     // White text
        ),
        shape = RoundedCornerShape(10.dp)  // Rounded corners
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

