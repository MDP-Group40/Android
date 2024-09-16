package com.example.mdpandroid.ui.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mdpandroid.R
import com.example.mdpandroid.ui.car.CarViewModel

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
