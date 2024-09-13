package com.example.mdpandroid.domain

import com.example.mdpandroid.data.model.Obstacle
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// Define a sealed class for polymorphic serialization
@Serializable
sealed class BluetoothMessage {

    abstract val senderName: String
    abstract val isFromLocalUser: Boolean

    @Serializable
    @SerialName("text")
    data class TextMessage(
        val message: String,
        override val senderName: String,
        override val isFromLocalUser: Boolean
    ) : BluetoothMessage() {
        override fun toString(): String {
            return message
        }
    }

    @Serializable
    @SerialName("info")
    data class InfoMessage(
        val message: String,
        override val senderName: String,
        override val isFromLocalUser: Boolean
    ) : BluetoothMessage() {
        override fun toString(): String {
            return message
        }
    }

    @Serializable
    @SerialName("obstacles")
    data class ObstacleMessage(
        val obstacles: List<Obstacle>,
        override val senderName: String,
        override val isFromLocalUser: Boolean
    ) : BluetoothMessage() {
        override fun toString(): String {
            return "obstacles=${obstacles.size}"
        }
    }
}
