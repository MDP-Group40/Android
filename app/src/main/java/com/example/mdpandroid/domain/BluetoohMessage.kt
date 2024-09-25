package com.example.mdpandroid.domain

import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.Obstacle
import com.example.mdpandroid.data.model.Orientation
import com.example.mdpandroid.data.model.Target
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val obstacles: List<Obstacle>,
    val car: Car,
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
    val nextX: Float,
    val nextY: Float,
    val nextOrientation: String,
    val direction: String,
    val distance: Float,
    override val senderName: String,
    override val isFromLocalUser: Boolean
) : BluetoothMessage() {
    override fun toString(): String {
        return "New Car Coord = ($nextX, $nextY), Next Facing = $nextOrientation Direction to move = $direction, Distance to move =$distance"
    }
}

@Serializable
@SerialName("image")
data class ImageMessage(
    val targetId: Int,
    val numberOnObstacle: Int,
    override val senderName: String,
    override val isFromLocalUser: Boolean
) : BluetoothMessage() {
    override fun toString(): String {
        return "IMAGE MESSAGE received: targetId = $targetId, numberOnObstacle = $numberOnObstacle"
    }
}

