package com.example.mdpandroid.data.model

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

data class Car(
    val width: Float = 2f,  // Width and height can remain floats to match the coordinate system
    val height: Float = 3f,
    override var positionX: Float = 1f,  // Changed to Float
    override var positionY: Float = 1.5f,  // Changed to Float
    var orientation: Orientation = Orientation.NORTH,
    var rotationAngle: Float = 0f // Track the current rotation angle
) : Grid(positionX, positionY)
