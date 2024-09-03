package com.example.mdpandroid.ui.simulator

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.Orientation
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CarViewModel(private val sharedViewModel: SharedViewModel) : ViewModel() {

    private val gridSize get() = sharedViewModel.gridSize
    private val car get() = sharedViewModel.car
    private val obstacles get() = sharedViewModel.obstacles

    private val movementStepMajor = 0.5f   // Major step (1 grid unit)
    private val movementStepMinor = 0.25f // Minor step (0.5 grid unit)

    // Flags for movement
    private var isMovingForward = false
    private var isMovingBackward = false
    private var isTurningLeft = false
    private var isTurningRight = false

    // Job for managing the loop
    private var movementJob: Job? = null

    fun onMoveForward() {
        isMovingForward = true
        startMovementLoop()
    }

    fun onMoveBackward() {
        isMovingBackward = true
        startMovementLoop()
    }

    fun onMoveLeft() {
        isTurningLeft = true
        startMovementLoop()
    }

    fun onMoveRight() {
        isTurningRight = true
        startMovementLoop()
    }

    fun onStopMove() {
        isMovingForward = false
        isMovingBackward = false
        isTurningLeft = false
        isTurningRight = false
        stopMovementLoop()
    }

    private fun startMovementLoop(angleChange: Float = 22.5f) {
        if (movementJob == null && car.value != null) {
            movementJob = viewModelScope.launch {
                while (true) {
                    car.value?.let { currentCar ->
                        Log.d("MovementLoop", "Car position: (${currentCar.positionX}, ${currentCar.positionY}), Rotation: ${currentCar.rotationAngle}")
                        if (isMovingForward) {
                            Log.d("MovementLoop", "Moving Forward")
                            moveForward(currentCar)
                        }
                        if (isMovingBackward) {
                            Log.d("MovementLoop", "Moving Backward")
                            moveBackward(currentCar)
                        }
                        if (isTurningLeft) {
                            Log.d("MovementLoop", "Turning Left")
                            rotateCar(currentCar, -angleChange)
                        }
                        if (isTurningRight) {
                            Log.d("MovementLoop", "Turning Right")
                            rotateCar(currentCar, angleChange)
                        }
                    }
                    delay(100L)
                }
            }
        }
        else{
            stopMovementLoop()
        }
    }

    private fun stopMovementLoop() {
        movementJob?.cancel()
        movementJob = null
    }

    private fun moveForward(carPosition: Car) {
        val newPosition = when (carPosition.orientation) {
            Orientation.NORTH -> carPosition.copy(positionY = carPosition.positionY - movementStepMajor)
            Orientation.NORTHNORTHEAST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMajor,
                positionX = carPosition.positionX + movementStepMinor
            )
            Orientation.NORTHEAST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMajor,
                positionX = carPosition.positionX + movementStepMajor
            )
            Orientation.NORTHEASTEAST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMinor,
                positionX = carPosition.positionX + movementStepMajor
            )
            Orientation.EAST -> carPosition.copy(positionX = carPosition.positionX + movementStepMajor)
            Orientation.SOUTHEASTEAST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMinor,
                positionX = carPosition.positionX + movementStepMajor
            )
            Orientation.SOUTHEAST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMajor,
                positionX = carPosition.positionX + movementStepMajor
            )
            Orientation.SOUTHSOUTHEAST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMajor,
                positionX = carPosition.positionX + movementStepMinor
            )
            Orientation.SOUTH -> carPosition.copy(positionY = carPosition.positionY + movementStepMajor)
            Orientation.SOUTHSOUTHWEST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMajor,
                positionX = carPosition.positionX - movementStepMinor
            )
            Orientation.SOUTHWEST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMajor,
                positionX = carPosition.positionX - movementStepMajor
            )
            Orientation.SOUTHWESTWEST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMinor,
                positionX = carPosition.positionX - movementStepMajor
            )
            Orientation.WEST -> carPosition.copy(positionX = carPosition.positionX - movementStepMajor)
            Orientation.NORTHWESTWEST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMinor,
                positionX = carPosition.positionX - movementStepMajor
            )
            Orientation.NORTHWEST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMajor,
                positionX = carPosition.positionX - movementStepMajor
            )
            Orientation.NORTHNORTHWEST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMajor,
                positionX = carPosition.positionX - movementStepMinor
            )
        }

        // Check for obstacle collision
        if (!isObstacleInPath(newPosition)) {
            // Adjust halfWidth and halfHeight based on the orientation
            val (halfWidth, halfHeight) = getHalfDimensions(carPosition.orientation, carPosition.width, carPosition.height)

            if (newPosition.positionX - halfWidth >= 0f && newPosition.positionX + halfWidth <= gridSize &&
                newPosition.positionY - halfHeight >= 0f && newPosition.positionY + halfHeight <= gridSize
            ) {
                car.value = newPosition
            }
        }
    }

    private fun moveBackward(carPosition: Car) {

        val newPosition = when (carPosition.orientation) {
            Orientation.NORTH -> carPosition.copy(positionY = carPosition.positionY + movementStepMajor)
            Orientation.NORTHNORTHEAST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMajor,
                positionX = carPosition.positionX - movementStepMinor
            )
            Orientation.NORTHEAST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMajor,
                positionX = carPosition.positionX - movementStepMajor
            )
            Orientation.NORTHEASTEAST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMinor,
                positionX = carPosition.positionX - movementStepMajor
            )
            Orientation.EAST -> carPosition.copy(positionX = carPosition.positionX - movementStepMajor)
            Orientation.SOUTHEASTEAST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMinor,
                positionX = carPosition.positionX - movementStepMajor
            )
            Orientation.SOUTHEAST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMajor,
                positionX = carPosition.positionX - movementStepMajor
            )
            Orientation.SOUTHSOUTHEAST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMajor,
                positionX = carPosition.positionX - movementStepMinor
            )
            Orientation.SOUTH -> carPosition.copy(positionY = carPosition.positionY - movementStepMajor)
            Orientation.SOUTHSOUTHWEST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMajor,
                positionX = carPosition.positionX + movementStepMinor
            )
            Orientation.SOUTHWEST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMajor,
                positionX = carPosition.positionX + movementStepMajor
            )
            Orientation.SOUTHWESTWEST -> carPosition.copy(
                positionY = carPosition.positionY - movementStepMinor,
                positionX = carPosition.positionX + movementStepMajor
            )
            Orientation.WEST -> carPosition.copy(positionX = carPosition.positionX + movementStepMajor)
            Orientation.NORTHWESTWEST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMinor,
                positionX = carPosition.positionX + movementStepMajor
            )
            Orientation.NORTHWEST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMajor,
                positionX = carPosition.positionX + movementStepMajor
            )
            Orientation.NORTHNORTHWEST -> carPosition.copy(
                positionY = carPosition.positionY + movementStepMajor,
                positionX = carPosition.positionX + movementStepMinor
            )
        }

        // Check for obstacle collision
        if (!isObstacleInPath(newPosition)) {
            // Adjust halfWidth and halfHeight based on the orientation
            val (halfWidth, halfHeight) = getHalfDimensions(carPosition.orientation, carPosition.width, carPosition.height)

            if (newPosition.positionX - halfWidth >= 0f && newPosition.positionX + halfWidth <= gridSize &&
                newPosition.positionY - halfHeight >= 0f && newPosition.positionY + halfHeight <= gridSize
            ) {
                car.value = newPosition
            }
        }
    }

    private fun isObstacleInPath(newPosition: Car): Boolean {
        val halfWidth = newPosition.width / 2
        val halfHeight = newPosition.height / 2
        val newCarPositionXRange = newPosition.positionX - halfWidth..newPosition.positionX + halfWidth
        val newCarPositionYRange = newPosition.positionY - halfHeight..newPosition.positionY + halfHeight

        return obstacles.any { obstacle ->
            val obstacleXRange = obstacle.positionX..obstacle.positionX + 1
            val obstacleYRange = obstacle.positionY..obstacle.positionY + 1

            newCarPositionXRange.intersects(obstacleXRange) && newCarPositionYRange.intersects(obstacleYRange)
        }
    }

    private fun ClosedFloatingPointRange<Float>.intersects(other: ClosedFloatingPointRange<Float>): Boolean {
        return this.start < other.endInclusive && this.endInclusive > other.start
    }

    private fun rotateCar(carPosition: Car, angleChange: Float) {
        var newAngle = (carPosition.rotationAngle + angleChange) % 360
        if (newAngle < 0) {
            newAngle += 360 // Keep angle in the range [0, 360)
        }

        val newOrientation = when {
            newAngle < 11.25 || newAngle >= 348.75 -> Orientation.NORTH
            newAngle >= 11.25 && newAngle < 33.75 -> Orientation.NORTHNORTHEAST
            newAngle >= 33.75 && newAngle < 56.25 -> Orientation.NORTHEAST
            newAngle >= 56.25 && newAngle < 78.75 -> Orientation.NORTHEASTEAST
            newAngle >= 78.75 && newAngle < 101.25 -> Orientation.EAST
            newAngle >= 101.25 && newAngle < 123.75 -> Orientation.SOUTHEASTEAST
            newAngle >= 123.75 && newAngle < 146.25 -> Orientation.SOUTHEAST
            newAngle >= 146.25 && newAngle < 168.75 -> Orientation.SOUTHSOUTHEAST
            newAngle >= 168.75 && newAngle < 191.25 -> Orientation.SOUTH
            newAngle >= 191.25 && newAngle < 213.75 -> Orientation.SOUTHSOUTHWEST
            newAngle >= 213.75 && newAngle < 236.25 -> Orientation.SOUTHWEST
            newAngle >= 236.25 && newAngle < 258.75 -> Orientation.SOUTHWESTWEST
            newAngle >= 258.75 && newAngle < 281.25 -> Orientation.WEST
            newAngle >= 281.25 && newAngle < 303.75 -> Orientation.NORTHWESTWEST
            newAngle >= 303.75 && newAngle < 326.25 -> Orientation.NORTHWEST
            newAngle >= 326.25 && newAngle < 348.75 -> Orientation.NORTHNORTHWEST
            else -> carPosition.orientation // Fallback, shouldn't happen
        }
        car.value = carPosition.copy(rotationAngle = newAngle, orientation = newOrientation)
    }

    private fun getHalfDimensions(orientation: Orientation, width: Float, height: Float): Pair<Float, Float> {
        return if (orientation in listOf(Orientation.NORTH, Orientation.SOUTH, Orientation.NORTHNORTHEAST, Orientation.SOUTHWEST, Orientation.NORTHWEST, Orientation.SOUTHEAST, Orientation.SOUTHSOUTHEAST, Orientation.NORTHNORTHWEST)) {
            width / 2 to height / 2
        } else {
            height / 2 to width / 2
        }
    }
}
