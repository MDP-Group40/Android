package com.example.mdpandroid.ui.grid

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.mdpandroid.data.model.Facing
import com.example.mdpandroid.data.model.GameControlMode
import com.example.mdpandroid.ui.buttons.ControlViewModel
import com.example.mdpandroid.ui.shared.SharedViewModel
import com.example.mdpandroid.ui.sidebar.SidebarViewModel

class DirectionSelectorViewModel(
    private val sidebarViewModel: SidebarViewModel,
    private val sharedViewModel: SharedViewModel,
) : ControlViewModel() {

    // Track the position of the obstacle being edited
    private var editingObstaclePosition by mutableStateOf<Pair<Float, Float>?>(null)

    // Current selected facing
    var currentFacing: Facing? by mutableStateOf(null)

    // Original facing before any changes, to revert if necessary
    private var originalFacing: Facing? by mutableStateOf(null)

    // Initialize facing selection with the original facing direction
    private fun startFacingSelection(initialFacing: Facing?) {
        originalFacing = initialFacing
        currentFacing = initialFacing
        Log.d("DirectionSelectorVM", "Started facing selection with initial facing: $initialFacing")
    }

    // Handle the movement buttons for direction selection
    override fun handleButtonUp() {
        currentFacing = Facing.NORTH
        Log.d("DirectionSelectorVM", "Facing set to NORTH")
    }

    override fun handleButtonDown() {
        currentFacing = Facing.SOUTH
        Log.d("DirectionSelectorVM", "Facing set to SOUTH")
    }

    override fun handleButtonLeft() {
        currentFacing = Facing.WEST
        Log.d("DirectionSelectorVM", "Facing set to WEST")
    }

    override fun handleButtonRight() {
        currentFacing = Facing.EAST
        Log.d("DirectionSelectorVM", "Facing set to EAST")
    }

    // Confirm the selected direction (Button A)
    override fun handleButtonA() {
        originalFacing = currentFacing
        Log.d("DirectionSelectorVM", "Confirmed selection: $currentFacing")

        // Update the obstacle's facing direction using the SidebarViewModel
        editingObstaclePosition?.let { (x, y) ->
            sidebarViewModel.updateObstacleFacingWithFacing(x, y, currentFacing)
        }
        stopEditingObstacleFacing()

        sharedViewModel.gameControlMode.value = GameControlMode.DRIVING
    }

    // Revert to the original facing direction (Button B)
    override fun handleButtonB() {
        currentFacing = originalFacing
        Log.d("DirectionSelectorVM", "Reverted to original facing: $originalFacing")

        // Restore the original facing and stop editing
        editingObstaclePosition?.let { (x, y) ->
            sidebarViewModel.updateObstacleFacingWithFacing(x, y, originalFacing)
        }
        stopEditingObstacleFacing()

        sharedViewModel.gameControlMode.value = GameControlMode.DRIVING
    }

    // Stop movement isn't applicable here, but can be overridden
    override fun handleStopMovement() {
        Log.d("DirectionSelectorVM", "Stop movement triggered")
    }

    // Start editing an obstacle's facing
    fun startEditingObstacleFacing(x: Float, y: Float) {
        editingObstaclePosition = Pair(x, y)
        sharedViewModel.gameControlMode.value = GameControlMode.FACING
        startFacingSelection(sidebarViewModel.getObstacleAt(x,y)?.facing)
        Log.d("DirectionSelectorVM", "Started editing obstacle facing at ($x, $y)")
    }

    // Stop editing the obstacle
    private fun stopEditingObstacleFacing() {
        editingObstaclePosition = null
        Log.d("DirectionSelectorVM", "Stopped editing obstacle")
    }

    // Check if the obstacle is being edited
    fun isEditingObstacle(x: Float, y: Float): Boolean {
        return editingObstaclePosition?.let { it.first == x && it.second == y } ?: false
    }
}
