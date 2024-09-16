package com.example.mdpandroid.ui.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mdpandroid.data.model.Modes
import com.example.mdpandroid.ui.safeNavigate
import com.example.mdpandroid.ui.shared.SharedViewModel

@Composable
fun BottomButtons(navController: NavHostController, sharedViewModel: SharedViewModel){

    fun handleStartClick(){
        if (sharedViewModel.mode.value != Modes.IDLE) navController.safeNavigate("start")
        else sharedViewModel.showSnackbar("Please select a mode", SnackbarDuration.Short)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { navController.safeNavigate("bluetooth") }) {
            Text(text = "Menu")
        }

        Spacer(modifier = Modifier.width(5.dp))

        Button(onClick = { handleStartClick() }) {
            Text(text = "Start")
        }
    }
}
