package org.ub.animations.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.math.sign

@Composable
fun FluidCarousel(
    children: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    if (children.isEmpty()) return

    val l = children.size
    var index by remember { mutableIntStateOf(0) }
    var dragIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragDirection by remember { mutableFloatStateOf(0f) }
    var dragCompleted by remember { mutableStateOf(false) }

    val edge = remember { FluidEdge(count = 25) }
    
    var pathRetrigger by remember { mutableIntStateOf(0) }
    
    var containerSize by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { frameTimeMillis ->
                edge.tick(frameTimeMillis)
                pathRetrigger++
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->
                containerSize = Size(
                    layoutCoordinates.size.width.toFloat(),
                    layoutCoordinates.size.height.toFloat()
                )
            }
            .pointerInput(containerSize) {
                detectDragGestures(
                    onDragStart = { offset ->
                        if (dragIndex != null && dragCompleted) {
                            index = dragIndex!!
                        }
                        dragIndex = null
                        dragOffset = offset
                        dragCompleted = false
                        dragDirection = 0f

                        edge.farEdgeTension = 0.0f
                        edge.edgeTension = 0.01f
                        edge.reset()
                    },
                    onDragEnd = {
                        edge.applyTouchOffset()
                    },
                    onDragCancel = {
                        edge.applyTouchOffset()
                    },
                    onDrag = { change, _ ->
                        val localPosition = change.position
                        var dx = localPosition.x - dragOffset.x

                        if (dragDirection == 0f && abs(dx) > 20f) {
                            dragDirection = sign(dx)
                            edge.side = if (dragDirection == 1f) Side.LEFT else Side.RIGHT
                            dragIndex = index - dragDirection.toInt()
                        }
                        
                        if (dragDirection == 0f) return@detectDragGestures

                        if (!dragCompleted) {
                            var availW = dragOffset.x
                            if (dragDirection == 1f) {
                                availW = containerSize.width - availW
                            }
                            val width = if (containerSize.width > 0) containerSize.width else 1f
                            val ratio = dx * dragDirection / availW

                            if (ratio > 0.8f && availW / width > 0.5f) {
                                dragCompleted = true
                                edge.farEdgeTension = 0.01f
                                edge.edgeTension = 0.0f
                                edge.applyTouchOffset()
                            }
                        }

                        if (dragCompleted) return@detectDragGestures

                        if (dragDirection == -1f) {
                            dx = containerSize.width + dx
                        }
                        edge.applyTouchOffset(Offset(dx, localPosition.y), containerSize)
                    }
                )
            }
    ) {
        val safeIndex = (index % l + l) % l
        Box(modifier = Modifier.fillMaxSize()) {
            children[safeIndex]()
        }

        val safeDragIndex = dragIndex?.let { (it % l + l) % l }
        if (safeDragIndex != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val trigger = pathRetrigger
                        shape = FluidShape(edge, containerSize, 10f)
                        clip = true
                    }
            ) {
                children[safeDragIndex]()
            }
        }

        SunAndMoon(
            index = dragIndex ?: index,
            isDragComplete = dragCompleted
        )
    }
}

class FluidShape(
    private val edge: FluidEdge,
    private val containerSize: Size,
    private val margin: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = edge.buildPath(containerSize, margin)
        return Outline.Generic(path)
    }
}
