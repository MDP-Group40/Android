package com.example.mdpandroid.data.model

import kotlinx.serialization.Serializable


enum class Facing {
    NORTH,
    EAST,
    SOUTH,
    WEST,
}

@Serializable
data class Obstacle(
    var positionX: Float,
    var positionY: Float,
    var targetID: Int,
    var orientation: Facing = Facing.NORTH,
    var numberOnObstacle: Int? = null, // number on object
    val width: Int = 1,
    val height: Int = 1
) {
    override fun toString(): String {
        return "Obstacle(x=$positionX, y=$positionY, targetID=$targetID)"
    }
}
