package com.example.mdpandroid.ui.car

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.Orientation
import com.example.mdpandroid.ui.buttons.ControlViewModel
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.*

class CarViewModel(
    private val sharedViewModel: SharedViewModel
) : ControlViewModel() {

    // CarViewModel-specific properties
    private val gridSize get() = sharedViewModel.gridSize
    val car get() = sharedViewModel.car
    private val obstacles get() = sharedViewModel.obstacles
    private val target get() = sharedViewModel.target

    private val movementStepMajor = 0.5f

    // Flags for movement
    private var isMovingForward = false
    private var isMovingBackward = false
    private var isTurningLeft = false
    private var isTurningRight = false

    // Job for managing the loop
    private var movementJob: Job? = null


    // Abstract methods from ControlViewModel, now implemented in CarViewModel
    override fun handleButtonUp() {
        resetAllMovementFlags() // Ensure only forward movement is active
        isMovingForward = true
        startMovementLoop()
    }

    override fun handleButtonDown() {
        resetAllMovementFlags() // Ensure only backward movement is active
        isMovingBackward = true
        startMovementLoop()
    }

    override fun handleButtonLeft() {
        resetAllMovementFlags() // Ensure only left turning is active
        isTurningLeft = true
        startMovementLoop()
    }

    override fun handleButtonRight() {
        resetAllMovementFlags() // Ensure only right turning is active
        isTurningRight = true
        startMovementLoop()
    }

    override fun handleButtonA() {
        // If button A is used for moving forward, this can call handleButtonUp
        handleButtonUp()
    }

    override fun handleButtonB() {
        // If button B is used for moving backward, this can call handleButtonDown
        handleButtonDown()
    }

    override fun handleStopMovement() {
        resetAllMovementFlags()
        stopMovementLoop()
    }

    private fun resetAllMovementFlags() {
        isMovingForward = false
        isMovingBackward = false
        isTurningLeft = false
        isTurningRight = false
    }

    private fun startMovementLoop(angleChange: Float = 22.5f) {
        if (movementJob == null && car.value != null) {
            movementJob = viewModelScope.launch {
                while (true) {
                    car.value?.let { currentCar ->
                        Log.d("MovementLoop", "Car position: (${currentCar.positionX}, ${currentCar.positionY}), Rotation: ${currentCar.rotationAngle}")

                        // Move the calculations off the main thread
                        val newPosition: Car? = withContext(Dispatchers.Default) {
                            when {
                                isMovingForward -> {
                                    Log.d("MovementLoop", "Moving Forward")
                                    moveForward(currentCar)
                                }
                                isTurningLeft -> {
                                    Log.d("MovementLoop", "Turning Left")
                                    rotateCar(currentCar, -angleChange)
                                }
                                isTurningRight -> {
                                    Log.d("MovementLoop", "Turning Right")
                                    rotateCar(currentCar, angleChange)
                                }
                                isMovingBackward -> {
                                    Log.d("MovementLoop", "Moving Backward")
                                    moveBackward(currentCar)
                                }
                                else -> null
                            }
                        }

                        // Update car state on the main thread
                        withContext(Dispatchers.Main) {
                            if (newPosition != null) {
                                car.value = newPosition
                            }
                        }

                        // Check if any movement is still happening
                        if (!isMovingForward && !isMovingBackward && !isTurningLeft && !isTurningRight) {
                            stopMovementLoop()
                        }
                    }
                    delay(150L)  // This delay simulates the time between updates
                }
            }
        } else {
            stopMovementLoop()
        }
    }

    private fun stopMovementLoop() {
        movementJob?.cancel()
        movementJob = null
    }

    private suspend fun moveForward(carPosition: Car): Car {
        return withContext(Dispatchers.Default) {
            val newPosition = getNextGridPosition(carPosition, forward = true)
            if (isGridCellOccupied(newPosition)) {
                // If the grid cell is occupied or out of bounds, return the current position, preventing movement
                return@withContext carPosition
            }
            return@withContext newPosition
        }
    }

    private suspend fun moveBackward(carPosition: Car): Car {
        return withContext(Dispatchers.Default) {
            val newPosition = getNextGridPosition(carPosition, forward = false)
            if (isGridCellOccupied(newPosition)) {
                // If the grid cell is occupied or out of bounds, return the current position, preventing movement
                return@withContext carPosition
            }
            return@withContext newPosition
        }
    }

    // Calculate the next grid position based on the car's current orientation and movement direction
    private fun getNextGridPosition(car: Car, forward: Boolean): Car {
        val direction = if (forward) 1f else -1f
        val angleInRadians = car.rotationAngle * (PI / 180).toFloat()

        // Correcting the deltaX and deltaY movement
        val deltaX = direction * movementStepMajor * sin(angleInRadians)  // Y-axis influences X movement
        val deltaY = direction * movementStepMajor * cos(angleInRadians)  // X-axis influences Y movement

        // Rounding the new position to 2 decimal places using Locale.US
        val roundedX = String.format(Locale.US, "%.1f", car.positionX + deltaX).toFloat()
        val roundedY = String.format(Locale.US, "%.1f", car.positionY - deltaY).toFloat()

        return car.copy(
            positionX = roundedX,
            positionY = roundedY
        )
    }

    private fun rotateCar(carPosition: Car, angleChange: Float): Car {
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

        return carPosition.copy(rotationAngle = newAngle, orientation = newOrientation)
    }

    private fun isGridCellOccupied(newPosition: Car): Boolean {
        val sideCenters = getSideCenters(newPosition)
        var isCollisionDetected = false

        Log.d("MovementLoop", "Checking grid cell occupancy for car position: (${newPosition.positionX}, ${newPosition.positionY}), Rotation: ${newPosition.rotationAngle}")
        Log.d("MovementLoop", "Side centers: $sideCenters")

        for ((centerX, centerY) in sideCenters) {
            val gridX = if (centerX % 1 >= 0.5) (centerX + 0.5).toInt() else centerX.toInt()
            val gridY = if (centerY % 1 >= 0.5) (centerY + 0.5).toInt() else centerY.toInt()

            Log.d("MovementLoop", "Checking side center at ($centerX, $centerY) -> Grid cell ($gridX, $gridY)")

            // Check if out of bounds
            if (gridX < 0 || gridX >= gridSize || gridY < 0 || gridY >= gridSize) {
                Log.d("MovementLoop", "Side center ($gridX, $gridY) is out of bounds!")
                isCollisionDetected = true
                break
            }

            // Check obstacle and target collisions
            val isObstacle = obstacles.any { obstacle ->
                val obstacleGridX = (obstacle.positionX).toInt()
                val obstacleGridY = (obstacle.positionY).toInt()
                val hit = gridX == obstacleGridX && gridY == obstacleGridY
                if (hit) {
                    Log.d("MovementLoop", "Side center ($gridX, $gridY) collides with obstacle at ($obstacleGridX, $obstacleGridY)")
                }
                hit
            }

            val isTarget = target.any { target ->
                val targetGridX = (target.positionX).toInt()
                val targetGridY = (target.positionY).toInt()
                val hit = gridX == targetGridX && gridY == targetGridY
                if (hit) {
                    Log.d("MovementLoop", "Side center ($gridX, $gridY) collides with target at ($targetGridX, $targetGridY)")
                }
                hit
            }

            if (isObstacle || isTarget) {
                isCollisionDetected = true
                break
            }
        }

        Log.d("MovementLoop", "Final result: Collision detected: $isCollisionDetected")
        return isCollisionDetected
    }


    private fun getDimensionsForOrientation(orientation: Orientation): Pair<Float, Float> {
        return when (orientation) {
            Orientation.NORTH, Orientation.SOUTH,Orientation.SOUTHSOUTHWEST,
            Orientation.SOUTHSOUTHEAST, Orientation.NORTHNORTHEAST,Orientation.NORTHNORTHWEST -> Pair(2f, 3f)  // width, height
            Orientation.EAST, Orientation.WEST,  Orientation.SOUTHEASTEAST,
            Orientation.SOUTHWESTWEST, Orientation.NORTHEASTEAST, Orientation.NORTHWESTWEST -> Pair(3f, 2f)  // width, height
            Orientation.NORTHEAST, Orientation.SOUTHEAST,
            Orientation.SOUTHWEST, Orientation.NORTHWEST -> Pair(1.5f, 2.5f)  // width, height

        }
    }

    private fun getSideCenters(car: Car): List<Pair<Float, Float>> {
        val (width, height) = getDimensionsForOrientation(car.orientation)

        val halfWidth = width / 2
        val halfHeight = height / 2

        val centerX = car.positionX
        val centerY = car.positionY

        // The four sides' centers (top, bottom, left, right) based on orientation
        return when (car.orientation) {
            // For orientations like NORTH and SOUTH, the standard top, bottom, left, and right apply
            Orientation.NORTH, Orientation.SOUTH, Orientation.SOUTHSOUTHEAST, Orientation.SOUTHSOUTHWEST,
            Orientation.NORTHNORTHWEST, Orientation.NORTHNORTHEAST -> listOf(
                Pair(centerX, centerY - halfHeight), // Top center
                Pair(centerX, centerY + halfHeight), // Bottom center
                Pair(centerX - halfWidth, centerY),  // Left center
                Pair(centerX + halfWidth, centerY)   // Right center
            )

            // For orientations like EAST and WEST, the "top" and "bottom" are on the X-axis (left and right)
            Orientation.EAST, Orientation.WEST, Orientation.SOUTHEASTEAST, Orientation.SOUTHWESTWEST,
            Orientation.NORTHEASTEAST, Orientation.NORTHWESTWEST -> listOf(
                Pair(centerX - halfHeight, centerY), // Left center (on the X-axis)
                Pair(centerX + halfHeight, centerY), // Right center (on the X-axis)
                Pair(centerX, centerY - halfWidth),  // Top center (on the Y-axis)
                Pair(centerX, centerY + halfWidth)   // Bottom center (on the Y-axis)
            )

            // For diagonal orientations like NORTHEAST, SOUTHEAST, NORTHWEST, and SOUTHWEST
            Orientation.NORTHEAST, Orientation.SOUTHEAST, Orientation.SOUTHWEST, Orientation.NORTHWEST -> listOf(
                Pair(centerX, centerY - 1.25f),  // Top center (based on height 2.5)
                Pair(centerX, centerY + 1.25f),  // Bottom center
                Pair(centerX - 0.75f, centerY),  // Left center (based on width 1.5)
                Pair(centerX + 0.75f, centerY)   // Right center
            )
        }
    }

}
