package com.example.mdpandroid.data.model

data class Obstacle(
    override var positionX: Float,
    override var positionY: Float,
    val width: Int = 1,
    val height: Int = 1,
) : Grid(positionX, positionY)
