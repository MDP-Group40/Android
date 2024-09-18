package com.example.mdpandroid.ui.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


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

    val buttonBackground: Painter = painterResource(id = imageResId)

    // Set background color based on press state
    val backgroundColor = if (isPressed) Color.LightGray else Color.Transparent


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
        shape = CircleShape,
        modifier = modifier.size(60.dp),  // Ensure the button has a fixed circular size
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = backgroundColor
        ),
        content = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)  // Clip to circle
                    .size(60.dp),  // Match size with the button itself
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = buttonBackground,  // Your image resource
                    contentDescription = "A Button",
                    modifier = Modifier
                        .size(50.dp)  // Ensure image is slightly smaller than button
                )

                // Only show the overlay when the button is pressed
                if (isPressed) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)  // Clip the overlay to the circular shape
                            .fillMaxSize()  // Make sure the overlay covers the full size of the button
                            .background(Color.Gray.copy(alpha = 0.5f))  // Semi-transparent grey overlay
                    )
                }
            }
        }
    )



}