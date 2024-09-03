package com.example.mdpandroid.ui.shared

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.mdpandroid.data.model.Car
import com.example.mdpandroid.data.model.Obstacle
import com.example.mdpandroid.data.model.Orientation

class SharedViewModel : ViewModel() {

    // Shared grid size
    val gridSize = 20f

    // Shared car state
    val car = mutableStateOf<Car?>(null)  // Now the car can be null

    // Shared obstacle list
    val obstacles = mutableStateListOf<Obstacle>()

    // Snackbar message state
    var snackbarMessage = mutableStateOf<String?>(null)
        private set

    // Method to trigger the Snackbar
    fun showSnackbar(message: String) {
        snackbarMessage.value = message
    }

    // Method to reset the Snackbar message after showing it
    fun resetSnackbar() {
        snackbarMessage.value = null
    }

    fun setCar(positionX: Float, positionY: Float, orientation: Orientation = Orientation.NORTH) {
        // Create a new Car instance
        val newCar = Car(positionX = positionX, positionY = positionY, orientation = orientation)

        // Set the rotation angle based on the orientation
        newCar.setRotationAngleBasedOnOrientation()

        // Update the car state with the new Car instance
        car.value = newCar
    }

    // Method to reset the car (e.g., when removing it)
    fun resetCar() {
        car.value = null
    }
}
