package com.example.mdpandroid.ui.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mdpandroid.R
import com.example.mdpandroid.ui.car.CarViewModel
import com.example.mdpandroid.ui.safeNavigate


@Composable
fun GameControls(viewModel: CarViewModel, navController: NavHostController, modifier: Modifier) {
    var activeButton by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(Color(0xFF04A9FC))
            .height(380.dp)
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
            DPad(viewModel, activeButton, setActiveButton = { activeButton = it })

            ABButton(viewModel = viewModel, activeButton = activeButton, setActiveButton = { activeButton = it} )
        }

        BottomButtons(navController = navController)
    }
}

@Composable
fun LeftRightTab(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-20).dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Left Button (L)
        Image(
            painter = painterResource(id = R.drawable.l_button),
            contentDescription = "L Button",
            modifier = Modifier
                .size(width = 200.dp, height = 65.dp) // Use rectangular size
                .clickable { navController.safeNavigate("grid") } // Apply clickable directly to the image
        )

        // Right Button (R)
        Image(
            painter = painterResource(id = R.drawable.r_button),
            contentDescription = "R Button",
            modifier = Modifier
                .size(width = 200.dp, height = 65.dp) // Use rectangular size
                .clickable { navController.safeNavigate("message") } // Apply clickable directly to the image
        )
    }
}


@Composable
fun BottomButtons(navController: NavHostController){

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { navController.safeNavigate("bluetooth") }) {
            Text(text = "Menu")
        }

        Spacer(modifier = Modifier.width(5.dp))

        Button(onClick = { navController.safeNavigate("start") }) {
            Text(text = "Start")
        }
    }

}


@Composable
fun ABButton(viewModel: CarViewModel, activeButton: String, setActiveButton: (String) -> Unit){
    // Right A and B buttons for moving forward/backward

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        MoveButton(
            onPress = { viewModel.onMoveForward() },
            onRelease = { viewModel.onStopMove() },
            label = "A",
            activeButton = activeButton,
            setActiveButton = { setActiveButton(it) },
            modifier = Modifier
                .size(120.dp)
                .offset(x = 40.dp),
            imageResId = R.drawable.a_button
        )

        Spacer(modifier = Modifier.height(20.dp))
        MoveButton(
            onPress = { viewModel.onMoveBackward() },
            onRelease = { viewModel.onStopMove() },
            label = "B",
            activeButton = activeButton,
            setActiveButton = { setActiveButton(it) },
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-30).dp),
            imageResId = R.drawable.b_button
        )
    }
}


@Composable
fun DPad(viewModel: CarViewModel, activeButton: String, setActiveButton: (String) -> Unit) {
    Box(
        modifier = Modifier
            .size(200.dp) // Adjust size as per your layout needs
    ) {
        // Background image for the DPad
        Image(
            painter = painterResource(id = R.drawable.pad_bg),
            contentDescription = "DPad Background",
            modifier = Modifier
                .fillMaxSize()
                .scale(1.4f)
                .offset(x = (-15).dp),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoveButton(
                onPress = { viewModel.onMoveForward() },
                onRelease = { viewModel.onStopMove() },
                label = "^",
                activeButton = activeButton,
                setActiveButton = { setActiveButton(it) },
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = 38.dp, y = (-28).dp),
                imageResId = R.drawable.up,
            )
        }

        Row {
            MoveButton(
                onPress = { viewModel.onMoveLeft() },
                onRelease = { viewModel.onStopMove() },
                label = "<",
                activeButton = activeButton,
                setActiveButton = { setActiveButton(it) },
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = (-40).dp, y = 52.dp),
                imageResId = R.drawable.left
            )

            MoveButton(
                onPress = { viewModel.onMoveRight() },
                onRelease = { viewModel.onStopMove() },
                label = ">",
                activeButton = activeButton,
                setActiveButton = { setActiveButton(it) },
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = 40.dp, y = 52.dp),
                imageResId = R.drawable.right
            )
        }

        MoveButton(
            onPress = { viewModel.onMoveBackward() },
            onRelease = { viewModel.onStopMove() },
            label = "_",
            activeButton = activeButton,
            setActiveButton = { setActiveButton(it) },
            modifier = Modifier
                .size(80.dp)
                .offset(x = 38.dp, y = 128.dp),
            imageResId = R.drawable.down
        )
    }
}





