package com.example.mdpandroid.ui.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mdpandroid.ui.car.CarViewModel
import androidx.compose.runtime.remember
import com.example.mdpandroid.ui.safeNavigate
import com.example.mdpandroid.R


@Composable
fun GameControls(viewModel: CarViewModel, navController: NavHostController, modifier: Modifier) {
    var activeButton by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(Color(0xFF04A9FC))
            .height(380.dp)
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LeftRightTab(navController = navController)
        Row(
            modifier = Modifier.fillMaxWidth(),
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
fun LeftRightTab(navController: NavHostController){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(onClick = { navController.safeNavigate("grid") }) {
            Text(text = "L")
        }
        Button(onClick = { navController.safeNavigate("message") }) {
            Text(text = "R")
        }
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
            modifier = Modifier.size(100.dp),
            imageResId = R.drawable.a
        )

        Spacer(modifier = Modifier.height(20.dp))
        MoveButton(
            onPress = { viewModel.onMoveBackward() },
            onRelease = { viewModel.onStopMove() },
            label = "B",
            activeButton = activeButton,
            setActiveButton = { setActiveButton(it) },
            modifier = Modifier
                .size(100.dp)
                .offset(x = (-80).dp),
            imageResId = R.drawable.b

        )
    }
}


@Composable
fun DPad(viewModel: CarViewModel, activeButton: String, setActiveButton: (String) -> Unit) {
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
                .padding(bottom = 10.dp),
            imageResId = R.drawable.up
        )

        Row {
            MoveButton(
                onPress = { viewModel.onMoveLeft() },
                onRelease = { viewModel.onStopMove() },
                label = "<",
                activeButton = activeButton,
                setActiveButton = { setActiveButton(it) },
                modifier = Modifier.size(80.dp),
                imageResId = R.drawable.left
            )
            Spacer(modifier = Modifier.width(80.dp))
            MoveButton(
                onPress = { viewModel.onMoveRight() },
                onRelease = { viewModel.onStopMove() },
                label = ">",
                activeButton = activeButton,
                setActiveButton = { setActiveButton(it) },
                modifier = Modifier.size(80.dp),
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
                .padding(top = 10.dp),
            imageResId = R.drawable.down
        )
    }
}





