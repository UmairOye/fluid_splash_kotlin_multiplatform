package org.ub.animations.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.moon_crescent
import kotlinproject.composeapp.generated.resources.sun_red
import kotlinproject.composeapp.generated.resources.sun_yellow
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SunAndMoon(
    index: Int,
    isDragComplete: Boolean,
    modifier: Modifier = Modifier
) {
    val rotationAnimation = remember { Animatable(1f) }
    var currentIndex by remember { mutableIntStateOf(0) }
    val rotationRadius = 300f

    LaunchedEffect(isDragComplete, index) {
        if (isDragComplete && index != currentIndex) {
            currentIndex = index
            val nextAnimState = index.toFloat() / 3f
            rotationAnimation.animateTo(
                targetValue = 1f - nextAnimState,
                animationSpec = tween(durationMillis = 350)
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val currentModIndex = currentIndex % 3
        
        BuildAssetWithDefaultAngle(
            imageRes = Res.drawable.sun_yellow,
            isVisible = 0 == currentModIndex,
            degreeAngle = 240f,
            rotationTurns = rotationAnimation.value,
            rotationRadius = rotationRadius
        )
        
        BuildAssetWithDefaultAngle(
            imageRes = Res.drawable.sun_red,
            isVisible = 1 == currentModIndex,
            degreeAngle = 30f,
            rotationTurns = rotationAnimation.value,
            rotationRadius = rotationRadius
        )
        
        BuildAssetWithDefaultAngle(
            imageRes = Res.drawable.moon_crescent,
            isVisible = 2 == currentModIndex,
            degreeAngle = 180f,
            rotationTurns = rotationAnimation.value,
            rotationRadius = rotationRadius
        )
    }
}

@Composable
private fun BuildAssetWithDefaultAngle(
    imageRes: DrawableResource,
    isVisible: Boolean,
    degreeAngle: Float,
    rotationTurns: Float,
    rotationRadius: Float
) {
    val radianAngle = degreeAngle / 180f * PI.toFloat()
    
    val targetAlpha = if (isVisible) 1f else 0f
    val currentAlpha = remember { Animatable(targetAlpha) }
    
    LaunchedEffect(isVisible) {
        currentAlpha.animateTo(
            targetValue = targetAlpha,
            animationSpec = tween(durationMillis = 300)
        )
    }
    
    val rotationDegrees = rotationTurns * 360f

    Box(
        modifier = Modifier
            .rotate(rotationDegrees)
            .offset {
                IntOffset(
                    (rotationRadius * cos(radianAngle)).toInt(),
                    (rotationRadius * sin(radianAngle)).toInt()
                )
            }
            .alpha(currentAlpha.value)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.size(70.dp)
        )
    }
}
