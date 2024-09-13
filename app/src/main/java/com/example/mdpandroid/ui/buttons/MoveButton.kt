package com.example.mdpandroid.ui.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.unit.dp
import com.example.mdpandroid.R


@Composable
fun MoveButton(
    onPress: () -> Unit,
    onRelease: () -> Unit,
    label: String,
    activeButton: String,
    setActiveButton: (String) -> Unit,
    modifier: Modifier = Modifier,
    imageResId: Int

) {
    // Use interactionSource to detect press state
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Set background color based on press state
    val backgroundColor = if (isPressed) Color.DarkGray else Color.LightGray

    // Handle onHold functionality with a coroutine
    LaunchedEffect(isPressed) {
        if (isPressed) {
            setActiveButton(label)
            while (isActive) {
                onPress() // Trigger the onPress function continuously while pressed
                delay(100L) // Adjust the delay as per the required frequency
            }
        } else if (activeButton == label) {
            onRelease() // Trigger onRelease when the button is released
        }
    }

    Button(
        onClick = { /* No action needed here */ },
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = 4.dp, // Set border width
                color = Color.Black, // Set border color
                shape = CircleShape // Ensure the border follows the round shape
            ),
    ) {

        val painter: Painter = painterResource(imageResId)

        Image(
            painter = painter, // Use the image from resources
            contentDescription = null, // Optional: Add description for accessibility
            modifier = Modifier.size(50.dp) // Adjust the image size
        )
    }
}
