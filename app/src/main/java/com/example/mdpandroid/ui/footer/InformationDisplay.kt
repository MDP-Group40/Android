package com.example.mdpandroid.ui.footer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mdpandroid.ui.shared.SharedViewModel

@Composable
fun InformationDisplay(
    viewModel: SharedViewModel,
    cellSize: Int = 50, // Default cell size
    showDebugInfo: Boolean = true, // Toggle to show debugging information
){
    val carPosition by viewModel.car
    val obstacles = viewModel.obstacles
    val targets = viewModel.target
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,  // Ensure equal spacing between columns
        verticalAlignment = Alignment.CenterVertically  // Align at the center vertically
    ) {

        Column(
            modifier = Modifier
                .weight(0.7f),
            verticalArrangement = Arrangement.Center
        ) {
            // Display car information
            DisplayStyle(text = "CAR INFORMATION")

            if (carPosition != null) {
                val car = carPosition!!
                DisplayStyle(text = "(X: ${car.x}, Y: ${car.y}) - ${car.orientation}")
            } else {
                DisplayStyle(text = "CAR IS NOT POSITIONED YET.")

            }
        }
        Column(
            modifier = Modifier.weight(0.1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "|")
            Text(text = "|")
        }
        Column(
            modifier = Modifier.weight(1f),
        ){
            DisplayStyle(text = "NUMBER OF OBSTACLES: ${obstacles.size}")

            LazyColumn(
                modifier = Modifier
                    .height(50.dp),
                verticalArrangement = Arrangement.Top, // Space between each message
                horizontalAlignment = Alignment.Start
            ) {
                // Reverse the order of obstacles for the LazyColumn
                items(obstacles.size) { index ->
                    // Reverse the index to show the largest number first
                    val reversedIndex = obstacles.size - 1 - index
                    val obstacle = obstacles[reversedIndex]
                    val num = reversedIndex + 1
                    val facing = obstacle.facing?: "UNKNOWN"  // Dereference MutableState for Facing

                    DisplayStyle(text = "  $num:(X: ${obstacle.x}, Y: ${obstacle.y}) - $facing", )
                }
            }
        }


        // }

        /* item {
                Text(text = "Grid Information: Number of Obstacles: ${obstacles.size}")
                Spacer(modifier = Modifier.height(8.dp)) // Add some space between items
                Text(text = "Obstacles:")
            }

            items(obstacles.size) { index ->
                val obstacle = obstacles[index]
                Text(text = "  $index: Position (X: ${obstacle.x}, Y: ${obstacle.y}), Facing: ${obstacle.facing}")
            }*/


    }
}