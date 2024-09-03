package com.example.mdpandroid.data.model

data class Obstacle(
    override var positionX: Float,
    override var positionY: Float,
    var targetID: Int,
    var numberOnObstacle:Int? =null, //number on object
    val width: Int = 1,
    val height: Int = 1,
) : Grid(positionX, positionY){
    override fun toString(): String {
        return "Obstacle(x=$positionX, y=$positionY, targetID=$targetID)"
    }
}
