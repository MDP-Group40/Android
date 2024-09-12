package com.example.mdpandroid.data.bluetooth

import android.bluetooth.BluetoothSocket
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
                return@flow
            }

            val buffer = ByteArray(1024) // Buffer to hold the incoming message
            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer) // Blocking call
                } catch (e: IOException) {
                    throw TransferFailedException()
                }

                // Convert the bytes to a string, then to a BluetoothMessage
                val incomingMessage = buffer.decodeToString(
                    endIndex = byteCount
                ).toBluetoothMessage(
                    isFromLocalUser = false // Set this based on your logic
                )

                emit(incomingMessage) // Emit the BluetoothMessage via Flow
            }
        }.flowOn(Dispatchers.IO) // Ensure the flow is on the IO dispatcher
    }

    private fun String.toBluetoothMessage(isFromLocalUser: Boolean): BluetoothMessage {
        // Deserialize JSON string back into BluetoothMessage
        val message = Json.decodeFromString<BluetoothMessage>(this)
        return message.copy(isFromLocalUser = isFromLocalUser) // Set the isFromLocalUser flag
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
