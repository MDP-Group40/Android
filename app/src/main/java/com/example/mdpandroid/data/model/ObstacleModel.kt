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
    var x: Float,
    var y: Float,
    var targetID: Int,
    var facing: Facing? = null,// Make sure `Facing` is serializable as well
    var numberOnObstacle: String? = null, // number on object
    val width: Int = 1,
    val height: Int = 1
) {
    override fun toString(): String {
        return "Obstacle(x=$x, y=$y, targetID=$targetID)"
    }
}
