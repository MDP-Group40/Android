package com.example.mdpandroid.data.model

data class Target(
    override var positionX: Float,
    override var positionY: Float,
    val width: Int = 1,
    val height: Int = 1,
) : Grid(positionX, positionY){
    override fun toString(): String {
        return "Target(x=$positionX, y=$positionY)"
    }
}