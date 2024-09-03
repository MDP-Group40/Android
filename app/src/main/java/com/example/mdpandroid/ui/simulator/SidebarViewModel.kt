package com.example.mdpandroid.ui.simulator

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mdpandroid.data.model.Obstacle
import com.example.mdpandroid.ui.shared.SharedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SidebarViewModel(private val sharedViewModel: SharedViewModel) : ViewModel(){

    private val gridSize get() = sharedViewModel.gridSize
    private val car get() = sharedViewModel.car
    private val obstacles get() = sharedViewModel.obstacles

    private var nextTargetID = 1 // Start the targetID from 1

    var showCoordinateDialog by mutableStateOf(false)
        private set

    var isAddingObstacle by mutableStateOf(false)
        private set

    private val _pendingSingleClick = MutableStateFlow(0L)
    val pendingSingleClick: StateFlow<Long> get() = _pendingSingleClick

    fun setPendingSingleClick(time: Long) {
        _pendingSingleClick.value = time
    }

    fun isObstaclePosition(x: Float, y: Float): Boolean {
        return obstacles.any { it.positionX == x && it.positionY == y }
    }

    fun toggleAddingObstacle() {
        isAddingObstacle = !isAddingObstacle
        Log.d("SimulatorViewModel", "Obstacle adding mode: $isAddingObstacle")
    }

    fun addObstacle(x: Float, y: Float) {
        if (x >= 0f && x < gridSize && y >= 0f && y < gridSize && !isObstaclePosition(x, y) && !isCarPosition(x, y)) {
            obstacles.add(Obstacle(x, y, nextTargetID))
            Log.d("SimulatorViewModel", "Obstacle added at ($x, $y) TargetID: $nextTargetID")
            nextTargetID++
        } else {
            Log.d("SimulatorViewModel", "Failed to add obstacle at ($x, $y): Position occupied or out of bounds")
        }
    }

    fun removeObstacle(x: Float, y: Float) {
        obstacles.removeAll { it.positionX == x && it.positionY == y }
        reassignTargetIDs() // Reassign target IDs after removal
        Log.d("SimulatorViewModel", "Obstacle removed at ($x, $y)")
    }

    fun getObstacleAt(x: Float, y: Float): Obstacle? {
        return obstacles.find { it.positionX == x && it.positionY == y }
    }

    fun showCoordinateDialog() {
        showCoordinateDialog = true
        Log.d("SimulatorViewModel", "Coordinate dialog shown.")
    }

    fun dismissCoordinateDialog() {
        showCoordinateDialog = false
        Log.d("SimulatorViewModel", "Coordinate dialog dismissed.")
    }

    private fun reassignTargetIDs() {
        nextTargetID = 1
        obstacles.forEach {
            it.targetID = nextTargetID++
        }
        Log.d("SimulatorViewModel", "Reassigned target IDs: $obstacles")
    }

    private fun isCarPosition(x: Float, y: Float): Boolean {
        val carPosition = car.value ?: return false  // Return false if the car is null
        val halfWidth = carPosition.width / 2
        val halfHeight = carPosition.height / 2
        return x >= (carPosition.positionX - halfWidth) && x < (carPosition.positionX + halfWidth) &&
                y >= (carPosition.positionY - halfHeight) && y < (carPosition.positionY + halfHeight)
    }


}