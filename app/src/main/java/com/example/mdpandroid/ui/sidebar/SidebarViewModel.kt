package com.example.mdpandroid.ui.sidebar

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mdpandroid.data.model.Facing
import com.example.mdpandroid.data.model.Modes
import com.example.mdpandroid.data.model.Obstacle
import com.example.mdpandroid.data.model.Target
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SidebarViewModel(private val sharedViewModel: SharedViewModel) : ViewModel(){

    private val gridSize get() = sharedViewModel.gridSize
    private val car get() = sharedViewModel.car
    private val obstacles get() = sharedViewModel.obstacles
    private val target get() = sharedViewModel.target

    var dialogForTarget by mutableStateOf(false)
        private set

    var dialogForObstacle by mutableStateOf(false)
        private set

    var isAddingObstacle by mutableStateOf(false)
        private set

    var isAddingTarget by mutableStateOf(false)
        private set

    // Add a mutable state to track the obstacle being edited
    var editingObstaclePosition by mutableStateOf<Pair<Float, Float>?>(null)
        private set

    // Toggle modes
    fun toggleMode(newMode: Modes) {
        sharedViewModel.mode.value = if (sharedViewModel.mode.value == newMode) Modes.IDLE else newMode
    }

    fun toggleDrivingMode() {
        sharedViewModel.drivingMode.value = !sharedViewModel.drivingMode.value
    }

    // Start editing an obstacle's facing
    fun startEditingObstacleFacing(x: Float, y: Float) {
        // Set the editing obstacle position to the clicked obstacle
        editingObstaclePosition = Pair(x, y)
        Log.d("SidebarViewModel", "Editing obstacle facing at ($x, $y)")
    }

    // Stop editing the obstacle (call this after the user finishes dragging or cancels)
    private fun stopEditingObstacleFacing() {
        editingObstaclePosition = null
        Log.d("SidebarViewModel", "Stopped editing obstacle")
    }

    // Check if an obstacle is being edited (this helps the UI know whether to enlarge an obstacle)
    fun isEditingObstacle(x: Float, y: Float): Boolean {
        return editingObstaclePosition?.let { it.first == x && it.second == y } ?: false
    }

    fun updateObstacleFacing(x: Float, y: Float, dx: Float, dy: Float) {
        val obstacle = getObstacleAt(x, y) ?: return

        // Determine the direction of the drag based on the larger movement
        val newFacing = when {
            kotlin.math.abs(dx) > kotlin.math.abs(dy) -> { // Greater horizontal movement
                if (dx > 0) Facing.EAST else Facing.WEST
            }
            kotlin.math.abs(dy) > kotlin.math.abs(dx) -> { // Greater vertical movement
                if (dy > 0) Facing.SOUTH else Facing.NORTH
            }
            else -> obstacle.facing // Keep the existing facing if no significant movement
        }

        obstacle.facing = newFacing // Update the state
        stopEditingObstacleFacing()
        Log.d("SidebarViewModel", "Updated obstacle facing at ($x, $y) to $newFacing")
    }

    fun isObstaclePosition(x: Float, y: Float): Boolean {
        return obstacles.any { it.x == x && it.y == y }
    }

    fun isTargetPosition(x: Float, y: Float): Boolean {
        return target.any { it.x == x && it.y == y }
    }

    fun toggleAddingObstacle() {
        isAddingObstacle = !isAddingObstacle
        Log.d("SimulatorViewModel", "Target adding mode: $isAddingObstacle")
    }

    fun toggleAddingTarget() {
        isAddingTarget = !isAddingTarget
        Log.d("SimulatorViewModel", "Obstacle adding mode: $isAddingObstacle")
    }

    fun addObstacle(x: Float, y: Float) {
        viewModelScope.launch(Dispatchers.Default) {
            if (x >= 0f && x < gridSize && y >= 0f && y < gridSize && !isObstaclePosition(x, y) && !isCarPosition(x, y) && !isTargetPosition(x, y)) {
                // Perform the add operation in the background
                obstacles.add(Obstacle(x, y, sharedViewModel.nextTargetId))

                // Reassign IDs if needed
                withContext(Dispatchers.Main) {
                    Log.d("SimulatorViewModel", "Obstacle added at ($x, $y) TargetID: $sharedViewModel.nextTargetId")
                }
                sharedViewModel.nextTargetId++
            } else {
                withContext(Dispatchers.Main) {
                    Log.d("SimulatorViewModel", "Failed to add obstacle at ($x, $y): Position occupied or out of bounds")
                }
            }
        }
    }


    fun addTarget(x: Float, y: Float) {
        if (x >= 0f && x < gridSize && y >= 0f && y < gridSize && !isObstaclePosition(x, y) && !isCarPosition(x, y) && !isTargetPosition(x,y)) {
            target.add(Target(x, y))
            Log.d("SimulatorViewModel", "Target added at ($x, $y)")
        } else {
            Log.d("SimulatorViewModel", "Failed to add Target at ($x, $y): Position occupied or out of bounds")
        }
    }

    fun removeObstacle(x: Float, y: Float) {
        viewModelScope.launch(Dispatchers.Default) {
            // Perform the removal in the background
            obstacles.removeAll { it.x == x && it.y == y }

            // Reassign IDs after removal
            reassignTargetIDs()

            // Log the result on the main thread
            withContext(Dispatchers.Main) {
                Log.d("SimulatorViewModel", "Obstacle removed at ($x, $y)")
            }
        }
    }

    fun removeTarget(x: Float, y: Float) {
        target.removeAll { it.x == x && it.y == y }
        Log.d("SimulatorViewModel", "Target removed at ($x, $y)")
    }

    fun getObstacleAt(x: Float, y: Float): Obstacle? {
        return obstacles.find { it.x == x && it.y == y }
    }

//    fun getTargetAt(x: Float, y: Float): Target? {
//        return target.find { it.positionX == x && it.positionY == y }
//    }

    fun showCoordinateDialogForTarget() {
        dialogForTarget = true
    }

    fun dismissCoordinateDialog() {
        dialogForTarget = false
        dialogForObstacle = false
    }

    fun showCoordinateDialogForObstacle() {
        dialogForObstacle = true
    }

    private fun reassignTargetIDs() {
        viewModelScope.launch(Dispatchers.Default) {
            sharedViewModel.nextTargetId = 1
            obstacles.forEach {
                it.targetID = sharedViewModel.nextTargetId++
            }
            withContext(Dispatchers.Main) {
                Log.d("SimulatorViewModel", "Reassigned target IDs: $obstacles")
            }
        }
    }

    private fun isCarPosition(x: Float, y: Float): Boolean {
        val carPosition = car.value ?: return false  // Return false if the car is null
        val halfWidth = carPosition.width / 2
        val halfHeight = carPosition.height / 2
        return x > (carPosition.x - halfWidth) && x < (carPosition.y + halfWidth) &&
                y > (carPosition.x - halfHeight) && y < (carPosition.y + halfHeight)
    }

}