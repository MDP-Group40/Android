package com.example.mdpandroid.ui.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mdpandroid.R
import com.example.mdpandroid.ui.safeNavigate

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
                .clickable { navController.safeNavigate("grid") } // Apply clickable directly to the image
        )

        // Right Button (R)
        Image(
            painter = painterResource(id = R.drawable.r_button),
            contentDescription = "R Button",
            modifier = Modifier
                .clickable { navController.safeNavigate("message") } // Apply clickable directly to the image
        )
    }
}