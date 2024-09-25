package com.example.mdpandroid.data.model

import kotlinx.serialization.Serializable

enum class Orientation {
    N,
    NNE, NE, NEE,
    E,
    SEE, SE, SSE,
    S,
    SSW, SW, SWW,
    W,
    NWW, NW, NNW
}

@Serializable
data class Car(
    val width: Float = 2f,  // Width and height can remain floats to match the coordinate system
    val height: Float = 3f,
    var x: Float,  // x-coordinate of the center of the car
    var y: Float,  // y-coordinate of the center of the car
    var transformY: Float, // use to render the car in terms of origin being bottom left
    var orientation: Orientation,
    var rotationAngle: Float = 0f // Track the current rotation angle
) {

    // Function to set rotationAngle based on the current orientation
    fun setRotationAngleBasedOnOrientation() {
        rotationAngle = when (orientation) {
            Orientation.N -> 0f
            Orientation.NNE -> 22.5f
            Orientation.NE -> 45f
            Orientation.NEE -> 67.5f
            Orientation.E -> 90f
            Orientation.SEE -> 112.5f
            Orientation.SE -> 135f
            Orientation.SSE -> 157.5f
            Orientation.S -> 180f
            Orientation.SSW -> 202.5f
            Orientation.SW -> 225f
            Orientation.SWW -> 247.5f
            Orientation.W -> 270f
            Orientation.NWW -> 292.5f
            Orientation.NW -> 315f
            Orientation.NNW -> 337.5f
        }
    }
}

