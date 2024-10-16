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

    // Increase buffer capacity for handling multiple incoming messages
    private val bluetoothMessageChannel = Channel<BluetoothMessage>(capacity = Channel.BUFFERED)

    // Function to listen for incoming messages
    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        Log.d("Bluetooth", "Starting to listen for incoming messages")

        if (!socket.isConnected) {
            Log.e("Bluetooth", "Socket is not connected. Exiting.")
            return bluetoothMessageChannel.receiveAsFlow()
        }

        val buffer = ByteArray(1024)  // Buffer to hold the incoming message

        // Coroutine to listen for incoming messages and add them to the channel
        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (true) {
                    val byteCount = socket.inputStream.read(buffer)
                    if (byteCount > 0) {
                        val incomingMessage = buffer.decodeToString(endIndex = byteCount)
                        val bluetoothMessage = try {
                            SerializationConfig.json.decodeFromString<BluetoothMessage>(incomingMessage)
                        } catch (e: Exception) {
                            Log.e("Bluetooth", "Failed to deserialize message: ${e.message}")
                            continue
                        }

                        // Enqueue the message into the channel
                        Log.d("Bluetooth", "Enqueuing received message: $bluetoothMessage")
                        bluetoothMessageChannel.trySend(bluetoothMessage)
                    } else {
                        Log.e("Bluetooth", "No data read from the stream.")
                    }
                }
            } catch (e: IOException) {
                Log.e("Bluetooth", "Failed to read from input stream: ${e.message}")
                bluetoothMessageChannel.close(e)
            }
        }

        return bluetoothMessageChannel.receiveAsFlow()
    }

    // Function to send a message
    suspend fun sendBluetoothMessage(bluetoothMessage: BluetoothMessage): Boolean {
        val messageJson = Json.encodeToString(bluetoothMessage)
        val messageBytes = messageJson.toByteArray()

        return sendMessage(messageBytes)
    }

    // Send raw data to the Bluetooth socket
    private suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes)
                Log.d("Bluetooth", "Message sent successfully.")
                true
            } catch (e: IOException) {
                Log.e("Bluetooth", "Failed to send message: ${e.message}")
                false
            }
        }
    }

    // Close the socket and cleanup
    fun close() {
        try {
            socket.close()
            Log.d("Bluetooth", "Socket closed successfully.")
        } catch (e: IOException) {
            Log.e("Bluetooth", "Failed to close the socket: ${e.message}")
        }
    }
}


