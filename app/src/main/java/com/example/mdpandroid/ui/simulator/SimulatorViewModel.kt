package com.example.mdpandroid.ui.simulator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.Obstacle
import com.example.mdpandroid.data.model.Orientation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class SimulatorViewModel : ViewModel() {

    val gridSize = 20f // Grid is 20x20

    val car = mutableStateOf(Car())
    val obstacles = mutableStateListOf<Obstacle>()
    var showCoordinateDialog by mutableStateOf(false)
        private set

    // Flags for movement
    private var isMovingForward = false
    private var isMovingBackward = false
    private var isTurningLeft = false
    private var isTurningRight = false

    // Job for managing the loop
    private var movementJob: Job? = null

    // Timer to stop movement after inactivity
    private var inactivityTimerJob: Job? = null

    // Track the grid's top-left corner position on the screen
    private var gridOffset by mutableStateOf(Offset.Zero)

    private val _dragPosition = mutableStateOf(Offset.Zero)
    val dragPosition: State<Offset> = _dragPosition

    fun onMoveForward() {
        startMovementLoop()
        resetInactivityTimer()
        isMovingForward = true
    }

    fun onMoveBackward() {
        startMovementLoop()
        resetInactivityTimer()
        isMovingBackward = true
    }

    fun onMoveLeft() {
        startMovementLoop()
        resetInactivityTimer()
        isTurningLeft = true
    }

    fun onMoveRight() {
        startMovementLoop()
        resetInactivityTimer()
        isTurningRight = true
    }

    fun onStopMove() {
        isMovingForward = false
        isMovingBackward = false
        isTurningLeft = false
        isTurningRight = false
    }

    private fun startMovementLoop() {
        if (movementJob == null) {
            movementJob = viewModelScope.launch {
                while (true) {
                    if (isMovingForward) moveForward(car.value)
                    if (isMovingBackward) moveBackward(car.value)
                    if (isTurningLeft) rotateCar(car.value, -22.5f)
                    if (isTurningRight) rotateCar(car.value, 22.5f)
                    // println("Car Center Position: (X: ${car.value.positionX}, Y: ${car.value.positionY}), Orientation: ${car.value.orientation}, Rotation: ${car.value.rotationAngle}")
                    delay(100L)
                }
            }
        }
    }

    private fun resetInactivityTimer() {
        inactivityTimerJob?.cancel() // Cancel any existing timer

        inactivityTimerJob = viewModelScope.launch {
            delay(5000L) // 5 seconds of inactivity
            stopMovementLoop()
        }
    }

    private fun stopMovementLoop() {
        movementJob?.cancel()
        movementJob = null
        onStopMove()
    }

    private fun moveForward(carPosition: Car) {
        val movementStepMajor = 0.5f   // Major step (1 grid unit)
        val movementStepMinor = 0.25f // Minor step (0.5 grid unit)
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
        val movementStepMajor = 0.5f   // Major step (1 grid unit)
        val movementStepMinor = 0.25f // Minor step (0.5 grid unit)
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

    fun isCarPosition(x: Float, y: Float): Boolean {
        val carPosition = car.value
        val halfWidth = carPosition.width / 2
        val halfHeight = carPosition.height / 2
        return x >= (carPosition.positionX - halfWidth) && x < (carPosition.positionX + halfWidth) &&
                y >= (carPosition.positionY - halfHeight) && y < (carPosition.positionY + halfHeight)
    }

    fun isObstaclePosition(x: Float, y: Float): Boolean {
        return obstacles.any { it.positionX == x && it.positionY == y }
    }

    fun addObstacle(x: Float, y: Float) {
        if (x >= 0f && x < gridSize && y >= 0f && y < gridSize && !isCarPosition(x, y)) {
            obstacles.add(Obstacle(x, y))
        }
    }

    fun showCoordinateDialog() {
        showCoordinateDialog = true
    }

    fun dismissCoordinateDialog() {
        showCoordinateDialog = false
    }

    fun startDragging(offset: Offset) {
        _dragPosition.value = offset
        println("Drag started at screen offset: $offset")
    }

    fun updateDragging(offset: Offset) {
        _dragPosition.value = offset
        println("Dragging to screen offset: $offset")
    }

    fun endDragging() {
        _dragPosition.value = Offset.Zero
        println("Drag ended.")
    }

    fun placeObstacleOnGrid(draggedOffset: Offset, gridOffset: Offset, density: Density) {
        val cellSizeDp = 15.dp
        val cellSizePx = with(density) { cellSizeDp.toPx() }

        // Calculate the relative offset
        val relativeOffset = draggedOffset - gridOffset
        val x = (relativeOffset.x / cellSizePx).toInt().toFloat()
        val y = (relativeOffset.y / cellSizePx).toInt().toFloat()

        println("Grid offset: $gridOffset")
        println("Dragged offset: $draggedOffset")
        println("Relative offset: $relativeOffset")
        println("Calculated grid position: x=$x, y=$y based on relative offset: $relativeOffset")

        // Check bounds and add obstacle
        if (x >= 0f && x < gridSize && y >= 0f && y < gridSize) {
            if (!isObstaclePosition(x, y) && !isCarPosition(x, y)) {
                addObstacle(x, y)
                println("Obstacle successfully placed at: x=$x, y=$y")
            } else {
                println("Obstacle placement failed: Position is occupied")
            }
        } else {
            println("Obstacle placement failed: x=$x, y=$y (Out of bounds)")
        }

        // Ensure to log completion
        println("PlaceObstacleOnGrid completed.")
    }

    fun updateGridOffset(offset: Offset) {
        gridOffset = offset
        println("Grid offset updated to: $gridOffset")
    }

}
