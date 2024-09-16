package com.example.mdpandroid.ui.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mdpandroid.R

@Composable
fun ABButton(viewModel: ControlViewModel,
             activeButton: String,
             setActiveButton: (String) -> Unit){
    // Right A and B buttons for moving forward/backward

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        MoveButton(
            onPress = {
                viewModel.handleButtonA()
            },
            onRelease = { viewModel.handleStopMovement() },
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
            onPress = {
                viewModel.handleButtonB()
            },
            onRelease = { viewModel.handleStopMovement() },
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
