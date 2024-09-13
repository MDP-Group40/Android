package com.example.mdpandroid.data.bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.mdpandroid.domain.BluetoothMessage
import com.example.mdpandroid.domain.TransferFailedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlinx.serialization.json.Json

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {

    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            if (!socket.isConnected) {
                Log.e("Bluetooth", "Socket is not connected. Exiting flow.")
                return@flow
            }

            val buffer = ByteArray(1024) // Buffer to hold the incoming message
            while (true) {
                val byteCount = try {
                    Log.d("Bluetooth", "Waiting for incoming data...")
                    socket.inputStream.read(buffer) // Blocking call
                } catch (e: IOException) {
                    Log.e("Bluetooth", "Failed to read from input stream: ${e.message}")
                    throw TransferFailedException()
                }

                if (byteCount > 0) {
                    // Convert the bytes to a string, then to a BluetoothMessage
                    val incomingMessage = buffer.decodeToString(
                        endIndex = byteCount
                    ).toBluetoothMessage(
                        isFromLocalUser = false // Set this based on your logic
                    )

                    Log.d("Bluetooth", "Received message: ${incomingMessage.message}")
                    emit(incomingMessage) // Emit the BluetoothMessage via Flow
                } else {
                    Log.d("Bluetooth", "No data read from the stream. Byte count is 0.")
                }
            }
        }.flowOn(Dispatchers.IO) // Ensure the flow is on the IO dispatcher
    }

    private fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
        return try {
            // Deserialize JSON string back into BluetoothMessage
            val message = Json.decodeFromString<BluetoothMessage>(this)
            Log.d("Bluetooth", "Message deserialized successfully: $message")
            message.copy(isFromLocalUser = isFromLocalUser) // Set the isFromLocalUser flag
        } catch (e: Exception) {
            Log.e("Bluetooth", "Failed to deserialize message: ${e.message}")
            throw e
        }
    }

    suspend fun sendMessage(bytes: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                socket.outputStream.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext false
            }

            true
        }
    }

    // Add this to ensure proper closure of resources
    fun close() {
        try {
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
