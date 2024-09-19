package com.example.mdpandroid.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Target(
    var x: Float,
    var y: Float,
    val width: Int = 1,
    val height: Int = 1
) {
    override fun toString(): String {
        return "Target(x=$x, y=$y)"
    }
}
