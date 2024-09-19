package com.example.mdpandroid.ui.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mdpandroid.ui.shared.SharedViewModel


@Composable
fun GameControls(
    viewModel: ControlViewModel,
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    modifier: Modifier) {
    var activeButton by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(Color(0xFF04A9FC))
            .height(310.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LeftRightTab(navController = navController)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left D-Pad for turning
            DPad(viewModel = viewModel, activeButton = activeButton, setActiveButton = { activeButton = it }, sharedViewModel = sharedViewModel)

            ABButton(viewModel = viewModel, activeButton = activeButton, setActiveButton = { activeButton = it}, sharedViewModel = sharedViewModel)
        }

        BottomButtons(navController = navController, sharedViewModel = sharedViewModel)
    }
}















