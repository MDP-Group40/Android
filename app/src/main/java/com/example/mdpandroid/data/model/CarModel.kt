package com.example.mdpandroid.data.model

import kotlinx.serialization.Serializable

enum class Orientation {
    NORTH,
    NORTHNORTHEAST, NORTHEAST, NORTHEASTEAST,
    EAST,
    SOUTHEASTEAST, SOUTHEAST, SOUTHSOUTHEAST,
    SOUTH,
    SOUTHSOUTHWEST, SOUTHWEST, SOUTHWESTWEST,
    WEST,
    NORTHWESTWEST, NORTHWEST, NORTHNORTHWEST
}

@Serializable
data class Car(
    val width: Float = 2f,  // Width and height can remain floats to match the coordinate system
    val height: Float = 3f,
    var positionX: Float,  // x-coordinate of the center of the car
    var positionY: Float,  // y-coordinate of the center of the car
    var orientation: Orientation,
    var rotationAngle: Float = 0f // Track the current rotation angle
) {

    // Function to set rotationAngle based on the current orientation
    fun setRotationAngleBasedOnOrientation() {
        rotationAngle = when (orientation) {
            Orientation.NORTH -> 0f
            Orientation.NORTHNORTHEAST -> 22.5f
            Orientation.NORTHEAST -> 45f
            Orientation.NORTHEASTEAST -> 67.5f
            Orientation.EAST -> 90f
            Orientation.SOUTHEASTEAST -> 112.5f
            Orientation.SOUTHEAST -> 135f
            Orientation.SOUTHSOUTHEAST -> 157.5f
            Orientation.SOUTH -> 180f
            Orientation.SOUTHSOUTHWEST -> 202.5f
            Orientation.SOUTHWEST -> 225f
            Orientation.SOUTHWESTWEST -> 247.5f
            Orientation.WEST -> 270f
            Orientation.NORTHWESTWEST -> 292.5f
            Orientation.NORTHWEST -> 315f
            Orientation.NORTHNORTHWEST -> 337.5f
        }
    }
}

