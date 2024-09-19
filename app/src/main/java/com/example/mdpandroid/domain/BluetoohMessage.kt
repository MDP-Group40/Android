package com.example.mdpandroid.domain

import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.Modes
import com.example.mdpandroid.data.model.Obstacle
import com.example.mdpandroid.data.model.Target
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Polymorphic

@Polymorphic
@Serializable
sealed class BluetoothMessage {
    abstract val senderName: String
    abstract val isFromLocalUser: Boolean
}

@Serializable
@SerialName("message")
data class TextMessage(
    val value: String,
    override val senderName: String,
    override val isFromLocalUser: Boolean
) : BluetoothMessage() {
    override fun toString(): String {
        return value
    }
}

@Serializable
@SerialName("info")
data class InfoMessage(
    val value: String,
    override val senderName: String,
    override val isFromLocalUser: Boolean
) : BluetoothMessage() {
    override fun toString(): String {
        return value
    }
}


@Serializable
@SerialName("start")
data class StartMessage(
    val car: Car,
    val obstacles: List<Obstacle>,
    val target: List<Target>,
    val mode: Int,
    override val senderName: String,
    override val isFromLocalUser: Boolean
) : BluetoothMessage() {
    override fun toString(): String {
        return "Car = (${car.x}, ${car.y}) Obstacle List=${obstacles.size} Target List = ${target.size}, mode = {$mode}"
    }
}

@Serializable
@SerialName("movement")
data class MovementMessage(
    val car: Car,
    val direction: String,
    override val senderName: String,
    override val isFromLocalUser: Boolean
) : BluetoothMessage() {
    override fun toString(): String {
        return "Car = (${car.x}, ${car.y}), Direction = $direction"
    }
}

