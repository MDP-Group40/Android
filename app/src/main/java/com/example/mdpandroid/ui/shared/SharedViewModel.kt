package com.example.mdpandroid.ui.shared

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.GameControlMode
import com.example.mdpandroid.data.model.Modes
import com.example.mdpandroid.data.model.Obstacle
import com.example.mdpandroid.data.model.Orientation
import com.example.mdpandroid.data.model.Target
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    // Shared grid size
    val gridSize = 20

    // Shared car state
    val car = mutableStateOf<Car?>(null)  // Now the car can be null

    // Shared obstacle list
    val obstacles = mutableStateListOf<Obstacle>()

    // Shared target ID
    var nextTargetId = 1

    // Shared target list
    val target = mutableStateListOf<Target>()

    // Mode set
    val mode = mutableStateOf(Modes.IDLE)

    val gameControlMode = mutableStateOf(GameControlMode.DRIVING)

    val drivingMode = mutableStateOf(false)

    // Snackbar message state
    var snackbarMessage = mutableStateOf<String?>(null)
        private set

    // Snackbar duration state
    var snackbarDuration = mutableStateOf<SnackbarDuration?>(null)
        private set

    // Method to trigger the Snackbar with a specified duration
    fun showSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Indefinite) {
        snackbarMessage.value = message
        snackbarDuration.value = duration
    }

    // Method to reset the Snackbar message and duration after showing it
    fun resetSnackbar() {
        snackbarMessage.value = null
        snackbarDuration.value = null
    }

    fun setCar(positionX: Float, positionY: Float, orientation: Orientation = Orientation.N) {
        // Create a new Car instance
        // Offset from the center to the bottom-left corner before rotation
        val halfWidth = 3 / 2
        val halfHeight = 3 / 2

        val transformY = gridSize - positionY

        val rotationAngle = when (orientation) {
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

        // Rotation angle in radians
        val angleInRadians = rotationAngle * (PI / 180).toFloat()

        // Apply rotation to the offset to get the new leftX and leftY
        val offsetX = -halfWidth * cos(angleInRadians) - halfHeight * sin(angleInRadians)
        val offsetY = -halfWidth * sin(angleInRadians) + halfHeight * cos(angleInRadians)

        // Rounding the new position to 2 decimal places using Locale.US
        val leftX = String.format(Locale.US, "%.1f", positionX + offsetX).toFloat()
        val leftY = String.format(Locale.US, "%.1f", positionY + offsetY).toFloat()

        val newCar = Car(x = positionX, y = positionY, transformY = transformY, orientation = orientation, leftX = leftX, leftY = leftY)

        // Set the rotation angle based on the orientation
        newCar.setRotationAngleBasedOnOrientation()

        // Update the car state with the new Car instance
        car.value = newCar
    }

    fun setNumberOnObstacle(targetID: Int, numberOnObstacle: Int) {
        // Find the obstacle with the given targetID
        val obstacle = obstacles.find { it.targetID == targetID }

        // If the obstacle is found, update the numberOnObstacle
        if (obstacle != null) {
            obstacle.numberOnObstacle = numberOnObstacle
            Log.d("SharedViewModel", "Set numberOnObstacle to $numberOnObstacle for obstacle with targetID: $targetID")
        } else {
            Log.d("SharedViewModel", "Obstacle with targetID $targetID not found")
        }
    }

    // Reset car position and orientation
    fun resetCar() {
        car.value = null // Reset car state to null
    }

    // Clear all obstacles
    fun resetObstacles() {
        obstacles.clear() // Clear the obstacle list
    }

    // Clear the target list
    fun resetTargets() {
        target.clear() // Clear the target list
    }

    // Reset mode to IDLE
    fun resetMode() {
        mode.value = Modes.IDLE // Reset mode back to IDLE
    }

    fun resetTargetId() {
        nextTargetId = 1
    }

}
