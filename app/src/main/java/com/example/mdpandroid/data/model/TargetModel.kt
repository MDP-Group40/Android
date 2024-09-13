package com.example.mdpandroid.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Target(
    var positionX: Float,
    var positionY: Float,
    val width: Int = 1,
    val height: Int = 1
) {
    override fun toString(): String {
        return "Target(x=$positionX, y=$positionY)"
    }
}
