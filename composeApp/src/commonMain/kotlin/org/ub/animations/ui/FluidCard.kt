package org.ub.animations.ui

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@Composable
fun FluidCard(
    color: String,
    altColor: Color,
    title: String,
    subtitle: String
) {
    val infiniteTransition = rememberInfiniteTransition()

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val scaleX = 1.2f + (sin(time) * 0.05f)
    val scaleY = 1.2f + (cos(time) * 0.07f)
    val offsetY = 20f + (cos(time) * 20f)

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        
        val bgImageRes = when (color) {
            "Red" -> Res.drawable.bg_red
            "Yellow" -> Res.drawable.bg_yellow
            "Blue" -> Res.drawable.bg_blue
            else -> Res.drawable.bg_red
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        (-(scaleX - 1f) / 2f * width).toInt(),
                        ((-(scaleY - 1f) / 2f * height) + offsetY).toInt()
                    )
                }
                .graphicsLayer {
                    this.scaleX = scaleX
                    this.scaleY = scaleY
                    this.transformOrigin = TransformOrigin(0f, 0f)
                }
        ) {
            Image(
                painter = painterResource(bgImageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 75.dp, bottom = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val illustrationRes = when (color) {
                    "Red" -> Res.drawable.illustration_red
                    "Yellow" -> Res.drawable.illustration_yellow
                    "Blue" -> Res.drawable.illustration_blue
                    else -> Res.drawable.illustration_red
                }
                
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(illustrationRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                val sliderRes = when (color) {
                    "Red" -> Res.drawable.slider_red
                    "Yellow" -> Res.drawable.slider_yellow
                    "Blue" -> Res.drawable.slider_blue
                    else -> Res.drawable.slider_red
                }
                
                Image(
                    painter = painterResource(sliderRes),
                    contentDescription = null,
                    modifier = Modifier.height(14.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        style = Typography.titleLarge,
                        color = Color.White
                    )
                    
                    Text(
                        text = subtitle,
                        textAlign = TextAlign.Center,
                        style = Typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
