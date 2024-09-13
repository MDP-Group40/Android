package com.example.mdpandroid.ui.shared

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.Obstacle
import com.example.mdpandroid.data.model.Orientation
import com.example.mdpandroid.data.model.Target
import com.example.mdpandroid.data.model.Modes

class SharedViewModel : ViewModel() {

    // Shared grid size
    val gridSize = 20f

    // Shared car state
    val car = mutableStateOf<Car?>(null)  // Now the car can be null

    // Shared obstacle list
    val obstacles = mutableStateListOf<Obstacle>()

    // Shared target ID
    var nextTargetId = 1;

    // Shared target list
    val target = mutableStateListOf<Target>()

    // Mode set
    val mode = mutableStateOf(Modes.IDLE)

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

    fun setCar(positionX: Float, positionY: Float, orientation: Orientation = Orientation.NORTH) {
        // Create a new Car instance
        val newCar = Car(positionX = positionX, positionY = positionY, orientation = orientation)

        // Set the rotation angle based on the orientation
        newCar.setRotationAngleBasedOnOrientation()

        // Update the car state with the new Car instance
        car.value = newCar
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

    fun resetTargetId(){
        nextTargetId = 1
    }
}
