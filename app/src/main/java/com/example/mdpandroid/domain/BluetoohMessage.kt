package com.example.mdpandroid.domain

import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.Modes
import com.example.mdpandroid.data.model.Obstacle
import com.example.mdpandroid.data.model.Target
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
        val car: Car,
        val obstacles: List<Obstacle>,
        val target: List<Target>,
        val mode: Modes,
        override val senderName: String,
        override val isFromLocalUser: Boolean
    ) : BluetoothMessage() {
        override fun toString(): String {
            return "Car = (${car.positionX}, ${car.positionY}) Obstacle List=${obstacles.size} Target List = ${target.size}, mode = {$mode}"
        }
    }
}
