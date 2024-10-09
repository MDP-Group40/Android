package com.example.mdpandroid.data.bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.mdpandroid.domain.BluetoothMessage
import com.example.mdpandroid.ui.SerializationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException


class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {

    // Define a channel to hold incoming Bluetooth messages
    private val bluetoothMessageChannel = Channel<BluetoothMessage>(capacity = Channel.UNLIMITED)

    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        Log.d("Bluetooth", "Getting ready to listen for incoming messages")

        if (!socket.isConnected) {
            Log.e("Bluetooth", "Socket is not connected. Exiting.")
            return bluetoothMessageChannel.receiveAsFlow() // Return an empty flow
        }

        val buffer = ByteArray(1024) // Buffer to hold the incoming message
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val byteCount = try {
                    Log.d("Bluetooth", "Waiting for incoming data...")
                    socket.inputStream.read(buffer) // Blocking call to read incoming data
                } catch (e: IOException) {
                    Log.e("Bluetooth", "Failed to read from input stream: ${e.message}")
                    bluetoothMessageChannel.close(e) // Close the channel if there's an error
                    return@launch
                }

                if (byteCount > 0) {
                    val incomingMessage = buffer.decodeToString(endIndex = byteCount)

                    try {
                        val bluetoothMessage = SerializationConfig.json.decodeFromString<BluetoothMessage>(incomingMessage)
                        Log.d("Bluetooth", "Received message: $bluetoothMessage")

                        bluetoothMessageChannel.trySend(bluetoothMessage) // Add to the channel
                    } catch (e: Exception) {
                        Log.e("Bluetooth", "Failed to deserialize message: ${e.message}")
                    }
                } else {
                    Log.d("Bluetooth", "No data read from the stream. Byte count is 0.")
                }
            }
        }

        return bluetoothMessageChannel.receiveAsFlow() // Return the flow of messages
    }

    // Helper function to send a BluetoothMessage by serializing it to JSON
    suspend fun sendBluetoothMessage(bluetoothMessage: BluetoothMessage): Boolean {
        val messageJson = Json.encodeToString(bluetoothMessage)
        val messageBytes = messageJson.toByteArray()

        return sendMessage(messageBytes)
    }

    // Function to send raw byte data to the Bluetooth socket
    private suspend fun sendMessage(bytes: ByteArray): Boolean {
        Log.d("Bluetooth", "Trying to send message in BluetoothDataTransferService")
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes) // Write bytes to the socket output stream
                Log.d("Bluetooth", "Message sent successfully.")
            } catch (e: IOException) {
                Log.e("Bluetooth", "Failed to send message: ${e.message}")
                return@withContext false
            }

            true
        }
    }

    // Close the socket to release resources
    fun close() {
        try {
            socket.close()
            Log.d("Bluetooth", "Socket closed successfully.")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Failed to close the socket: ${e.message}")
        }
    }
}

