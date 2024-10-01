package com.example.mdpandroid.ui.car

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.Orientation
import com.example.mdpandroid.domain.MovementMessage
import com.example.mdpandroid.ui.buttons.ControlViewModel
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.LinkedList
import java.util.Locale
import java.util.Queue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

    // Queue to store movement commands
    private val movementQueue: Queue<MovementMessage> = LinkedList()
    private var isProcessingQueue = false // To track if the queue is being processed


    // Abstract methods from ControlViewModel, now implemented in CarViewModel
    override fun handleButtonUp() {
        Log.d("CarViewModel", "handleButtonUp is called")
        startMovement({ isMovingForward = true })
    }

    override fun handleButtonDown() {
        Log.d("CarViewModel", "handleButtonDown is called")
        startMovement({ isMovingBackward = true })
    }

    override fun handleButtonLeft() {
        Log.d("CarViewModel", "handleButtonLeft is called")
        startMovement({ isTurningLeft = true })
    }

    override fun handleButtonRight() {
        Log.d("CarViewModel", "handleButtonRight is called")
        startMovement({ isTurningRight = true })
    }

    override fun handleButtonA() {
        // If button A is used for moving forward, this can call handleButtonUp
        handleButtonUp()

//        movementViaBluetooth("Forward left", 20f)
    }

    override fun handleButtonB() {
        // If button B is used for moving backward, this can call handleButtonDown
        handleButtonDown()

//        movementViaBluetooth("Backward left", 20f)
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

    private fun startMovement(
        setMovementFlag: () -> Unit,
        angleChange: Float = 22.5f
    ) {
        // Reset all flags and set the appropriate movement flag
        resetAllMovementFlags()
        setMovementFlag()

        // Launch a coroutine to manage the movement
        viewModelScope.launch {
            startMovementLoop(angleChange)
            delay(150L)
        }
    }

    private fun startMovementLoop(angleChange: Float = 22.5f) {
        // If a movement job already exists, cancel it before starting a new one
        movementJob?.cancel()

        // Ensure the car exists before starting the movement job
        car.value?.let { currentCar ->
            movementJob = viewModelScope.launch {
                while (isMovingForward || isMovingBackward || isTurningLeft || isTurningRight) {
                    Log.d("MovementLoop", "Car position: (${currentCar.x}, ${currentCar.y}), Rotation: ${currentCar.rotationAngle}")

                    // Move the calculations off the main thread
                    val newPosition: Car? = withContext(Dispatchers.Default) {
                        when {
                            isMovingForward -> moveForward(currentCar)
                            isMovingBackward -> moveBackward(currentCar)
                            isTurningLeft -> rotateCar(currentCar, -angleChange)
                            isTurningRight -> rotateCar(currentCar, angleChange)
                            else -> null
                        }
                    }

                    // Update car state on the main thread
                    newPosition?.let { updatedCar ->
                        withContext(Dispatchers.Main) {
                            // Update the car position
                            car.value = updatedCar

                            // Calculate and update leftX and leftY after the movement
                            calculateLeftCoordinates(updatedCar)
                        }
                    }

                    // Check if all movement flags are false and stop the loop
                    if (!isMovingForward && !isMovingBackward && !isTurningLeft && !isTurningRight) {
                        stopMovementLoop()
                        break
                    }

                    delay(150L)  // Delay to simulate the time between updates
                }
            }
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
        val roundedX = String.format(Locale.US, "%.1f", car.x + deltaX).toFloat()
        val roundedY = String.format(Locale.US, "%.1f", car.y + deltaY).toFloat()
        val roundTransformY = String.format(Locale.US, "%.1f", car.transformY - deltaY).toFloat()

        return car.copy(
            x = roundedX,
            y = roundedY,
            transformY = roundTransformY
        )
    }

    private fun rotateCar(carPosition: Car, angleChange: Float): Car {
        var newAngle = (carPosition.rotationAngle + angleChange) % 360
        if (newAngle < 0) {
            newAngle += 360 // Keep angle in the range [0, 360)
        }

        val newOrientation = when {
            newAngle < 11.25 || newAngle >= 348.75 -> Orientation.N
            newAngle >= 11.25 && newAngle < 33.75 -> Orientation.NNE
            newAngle >= 33.75 && newAngle < 56.25 -> Orientation.NE
            newAngle >= 56.25 && newAngle < 78.75 -> Orientation.NEE
            newAngle >= 78.75 && newAngle < 101.25 -> Orientation.E
            newAngle >= 101.25 && newAngle < 123.75 -> Orientation.SEE
            newAngle >= 123.75 && newAngle < 146.25 -> Orientation.SE
            newAngle >= 146.25 && newAngle < 168.75 -> Orientation.SSE
            newAngle >= 168.75 && newAngle < 191.25 -> Orientation.S
            newAngle >= 191.25 && newAngle < 213.75 -> Orientation.SSW
            newAngle >= 213.75 && newAngle < 236.25 -> Orientation.SW
            newAngle >= 236.25 && newAngle < 258.75 -> Orientation.SWW
            newAngle >= 258.75 && newAngle < 281.25 -> Orientation.W
            newAngle >= 281.25 && newAngle < 303.75 -> Orientation.NWW
            newAngle >= 303.75 && newAngle < 326.25 -> Orientation.NW
            newAngle >= 326.25 && newAngle < 348.75 -> Orientation.NNW
            else -> carPosition.orientation // Fallback, shouldn't happen
        }

        return carPosition.copy(rotationAngle = newAngle, orientation = newOrientation)
    }

    // Function to calculate leftX and leftY based on the car's x, y, and rotation angle
    private fun calculateLeftCoordinates(carPosition: Car) {
        val (carWidth, carHeight) = getDimensionsForOrientation(carPosition.orientation)

        // Offset from the center to the bottom-left corner before rotation
        val halfWidth = carWidth / 2
        val halfHeight = carHeight / 2

        // Rotation angle in radians
        val angleInRadians = carPosition.rotationAngle * (PI / 180).toFloat()

        // Apply rotation to the offset to get the new leftX and leftY
        val offsetX = -halfWidth * cos(angleInRadians) - halfHeight * sin(angleInRadians)
        val offsetY = -halfWidth * sin(angleInRadians) + halfHeight * cos(angleInRadians)

        // Rounding the new position to 2 decimal places using Locale.US
        carPosition.leftX = String.format(Locale.US, "%.1f", carPosition.x + offsetX).toFloat()
        carPosition.leftY = String.format(Locale.US, "%.1f", carPosition.y + offsetY).toFloat()
    }

    private fun isGridCellOccupied(newPosition: Car): Boolean {
        val sideCenters = getSideCenters(newPosition)
        var isCollisionDetected = false

        Log.d("MovementLoop", "Checking grid cell occupancy for car position: (${newPosition.x}, ${newPosition.y}), Rotation: ${newPosition.rotationAngle}")
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
                val obstacleGridX = (obstacle.x).toInt()
                val obstacleGridY = (obstacle.y).toInt()
                val hit = gridX == obstacleGridX && gridY == obstacleGridY
                if (hit) {
                    Log.d("MovementLoop", "Side center ($gridX, $gridY) collides with obstacle at ($obstacleGridX, $obstacleGridY)")
                }
                hit
            }

            val isTarget = target.any { target ->
                val targetGridX = (target.x).toInt()
                val targetGridY = (target.y).toInt()
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
        return Pair(3f, 3f)  // width, height
    }

    private fun getSideCenters(car: Car): List<Pair<Float, Float>> {
        val (width, height) = getDimensionsForOrientation(car.orientation)

        val halfWidth = width / 2
        val halfHeight = height / 2

        val centerX = car.x
        val centerY = car.y

        // The four sides' centers (top, bottom, left, right) based on orientation
        return when (car.orientation) {
            // For orientations like NORTH and SOUTH, the standard top, bottom, left, and right apply
            Orientation.N, Orientation.S, Orientation.SSE, Orientation.SSW,
            Orientation.NNW, Orientation.NNE -> listOf(
                Pair(centerX, centerY - halfHeight), // Top center
                Pair(centerX, centerY + halfHeight), // Bottom center
                Pair(centerX - halfWidth, centerY),  // Left center
                Pair(centerX + halfWidth, centerY)   // Right center
            )

            // For orientations like EAST and WEST, the "top" and "bottom" are on the X-axis (left and right)
            Orientation.E, Orientation.W, Orientation.SEE, Orientation.SWW,
            Orientation.NEE, Orientation.NWW -> listOf(
                Pair(centerX - halfHeight, centerY), // Left center (on the X-axis)
                Pair(centerX + halfHeight, centerY), // Right center (on the X-axis)
                Pair(centerX, centerY - halfWidth),  // Top center (on the Y-axis)
                Pair(centerX, centerY + halfWidth)   // Bottom center (on the Y-axis)
            )

            // For diagonal orientations like NORTHEAST, SOUTHEAST, NORTHWEST, and SOUTHWEST
            Orientation.NE, Orientation.SE, Orientation.SW, Orientation.NW -> listOf(
                Pair(centerX, centerY - 1.25f),  // Top center (based on height 2.5)
                Pair(centerX, centerY + 1.25f),  // Bottom center
                Pair(centerX - 0.75f, centerY),  // Left center (based on width 1.5)
                Pair(centerX + 0.75f, centerY)   // Right center
            )
        }
    }

    // Bluetooth Connection Movement

    // Function to add movement commands to the queue
    fun enqueueMovementMessage(message: MovementMessage) {
        movementQueue.offer(message) // Add the message to the queue
        processMovementQueue() // Start processing the queue if not already in progress
    }

    // Process the queue one command at a time
    private fun processMovementQueue() {
        if (isProcessingQueue || movementQueue.isEmpty()) return // If already processing or queue is empty, do nothing

        isProcessingQueue = true // Set the flag to indicate we're processing the queue

        viewModelScope.launch {
            while (movementQueue.isNotEmpty()) {
                val message = movementQueue.poll() // Get and remove the next command from the queue
                if (message != null) {
                    // Process the movement command
                    Log.d("CarViewModel", "Processing MovementMessage: $message")
                    movementViaBluetooth(
                        action = message.direction,
                        distance = message.distance,
                        nextX = message.nextX,
                        nextY = message.nextY,
                        nextOrientation = message.nextOrientation
                    )

                    // Wait for movement to complete before processing the next command
                    delay(1000L) // Adjust this delay based on movement time
                }
            }
            isProcessingQueue = false // Mark the queue as processed once it's done
        }
    }

    private fun movementViaBluetooth(
        action: String,
        distance:Float,
        nextX: Float = 0f,
        nextY: Float =0f,
        nextOrientation: String = ""
    ){

        Log.d("CarViewModel", "action = $action, distance = $distance")
        when (action){
            "Forward" -> straightMovement(distance = distance, forward = true)
            "Forward left" -> forwardLeft()
            "Forward right" -> forwardRight()
            "Backward" -> straightMovement(distance = distance, forward = false)
            "Backward left" -> backwardLeft()
            "Backward right" -> backwardRight()
            else -> {
                // do nothing
            }
        }
        val nextOri: Orientation = when (nextOrientation){
            "N" -> Orientation.N
            "S" -> Orientation.S
            "E" -> Orientation.E
            "W" -> Orientation.W
            else -> Orientation.N
        }

       car.value?.let { sharedViewModel.setCar(positionX = nextX, positionY = nextY, orientation = nextOri ) }
    }

    private fun straightMovement(distance: Float, forward: Boolean) {
        val stepsToMove = (distance / 5).toInt()

        if (stepsToMove > 0) {
            viewModelScope.launch {
                for (i in 0 until stepsToMove) {
                    if (forward) {
                        startMovement({ isMovingForward = true })
                    } else {
                        startMovement({ isMovingBackward = true })
                    }

                    delay(150L)
                    resetAllMovementFlags()
                }
            }
        }
    }

    private fun forwardRight() {
        actualCarTurningMovement(
            setVerticalMovementFlag = { isMovingForward = true },
            setHorizontalMovementFlag = { isTurningRight = true },
            forward = true
        )
    }

    private fun backwardRight() {
        actualCarTurningMovement(
            setVerticalMovementFlag = { isMovingBackward = true },
            setHorizontalMovementFlag = { isTurningLeft = true },
            forward = false
        )
    }

    private fun forwardLeft() {
        actualCarTurningMovement(
            setVerticalMovementFlag = { isMovingForward = true },
            setHorizontalMovementFlag = { isTurningLeft = true },
            forward = true
        )
    }

    private fun backwardLeft() {
        actualCarTurningMovement(
            setVerticalMovementFlag = { isMovingBackward = true },
            setHorizontalMovementFlag = { isTurningRight = true },
            forward = false
        )
    }

    private fun actualCarTurningMovement(
        setVerticalMovementFlag: () -> Unit,
        setHorizontalMovementFlag: () -> Unit,
        forward: Boolean
    ) {
        viewModelScope.launch {
            startMovement(setVerticalMovementFlag)
            delay(150L)

            startMovement(setVerticalMovementFlag)
            delay(150L)

            if (!forward) {
                startMovement(setVerticalMovementFlag)
                delay(150L)
            }

            startMovement(setHorizontalMovementFlag)
            delay(150L)

            if (!forward) {
                startMovement(setVerticalMovementFlag)
                delay(150L)
            }

            startMovement(setVerticalMovementFlag)
            delay(150L)

            startMovement(setHorizontalMovementFlag)
            delay(150L)

            startMovement(setVerticalMovementFlag)
            delay(150L)

            startMovement(setVerticalMovementFlag)
            delay(150L)

            startMovement(setHorizontalMovementFlag)
            delay(150L)

            startMovement(setVerticalMovementFlag)
            delay(150L)

            startMovement(setVerticalMovementFlag)
            delay(150L)

            startMovement(setVerticalMovementFlag)
            delay(150L)

            startMovement(setHorizontalMovementFlag)
            delay(150L)

            startMovement(setVerticalMovementFlag)
            delay(150L)

            if (forward) {
                startMovement(setVerticalMovementFlag)
                delay(150L)

                startMovement(setVerticalMovementFlag)
                delay(150L)
            }

            // After completing all movements, reset all flags to stop the movement
            resetAllMovementFlags()
        }
    }
}