package org.ub.animations

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.ub.animations.ui.FluidCard
import org.ub.animations.ui.FluidCarousel

@Composable
fun App() {
    MaterialTheme {
        FluidCarousel(
            modifier = Modifier.fillMaxSize(),
            children = listOf(
                {
                    FluidCard(
                        color = "Red",
                        altColor = Color(0xFFE23E57),
                        title = "Red Planet",
                        subtitle = "Exploration of the crimson world"
                    )
                },
                {
                    FluidCard(
                        color = "Yellow",
                        altColor = Color(0xFFFFD700),
                        title = "Golden Sun",
                        subtitle = "The bright star at our center"
                    )
                },
                {
                    FluidCard(
                        color = "Blue",
                        altColor = Color(0xFF005691),
                        title = "Deep Ocean",
                        subtitle = "Into the mysterious blue depths"
                    )
                }
            )
        )
    }
}