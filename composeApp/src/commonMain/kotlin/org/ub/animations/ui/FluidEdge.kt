package org.ub.animations.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

enum class Side {
    LEFT, TOP, RIGHT, BOTTOM
}

class FluidEdge(
    count: Int = 10,
    var side: Side = Side.LEFT
) {
    private val points = ArrayList<FluidPoint>()
    
    var edgeTension: Float = 0.01f
    var farEdgeTension: Float = 0.0f
    var touchTension: Float = 0.1f
    var pointTension: Float = 0.25f
    var damping: Float = 0.9f
    var maxTouchDistance: Float = 0.15f
    
    private var lastT: Long = 0
    var touchOffset: Offset? = null

    init {
        for (i in 0 until count) {
            points.add(FluidPoint(0f, i.toFloat() / (count - 1)))
        }
    }

    fun reset() {
        points.forEach { pt ->
            pt.x = 0f
            pt.velX = 0f
            pt.velY = 0f
        }
    }

    fun applyTouchOffset(offset: Offset? = null, size: Size = Size.Zero) {
        if (offset == null) {
            touchOffset = null
            return
        }
        val fraction = Offset(
            x = if (size.width > 0) offset.x / size.width else 0f,
            y = if (size.height > 0) offset.y / size.height else 0f
        )
        
        touchOffset = when (side) {
            Side.LEFT -> fraction
            Side.RIGHT -> Offset(1f - fraction.x, 1f - fraction.y)
            Side.TOP -> Offset(fraction.y, 1f - fraction.x)
            Side.BOTTOM -> Offset(1f - fraction.y, fraction.x)
        }
    }

    fun buildPath(size: Size, margin: Float = 0f): Path {
        if (points.isEmpty()) return Path()

        val mtx = getTransform(size, margin)
        val path = Path()
        val l = points.size

        var pt = FluidPoint(-margin, 1f).toOffset(mtx)
        path.moveTo(pt.x, pt.y)

        pt = FluidPoint(-margin, 0f).toOffset(mtx)
        path.lineTo(pt.x, pt.y)

        pt = points[0].toOffset(mtx)
        path.lineTo(pt.x, pt.y)

        var pt1 = points[1].toOffset(mtx)
        path.lineTo(
            pt.x + (pt1.x - pt.x) / 2f,
            pt.y + (pt1.y - pt.y) / 2f
        )

        for (i in 2 until l) {
            pt = pt1
            pt1 = points[i].toOffset(mtx)
            val midX = pt.x + (pt1.x - pt.x) / 2f
            val midY = pt.y + (pt1.y - pt.y) / 2f
            path.quadraticTo(pt.x, pt.y, midX, midY)
        }

        path.lineTo(pt1.x, pt1.y)
        path.close()

        return path
    }

    fun tick(durationMillis: Long) {
        if (points.isEmpty()) return
        
        val l = points.size
        
        if (lastT == 0L) lastT = durationMillis
        
        val t = min(1.5f, (durationMillis - lastT).toFloat() / 1000f * 60f)
        lastT = durationMillis
        val dampingT = damping.pow(t)

        for (i in 0 until l) {
            val pt = points[i]
            pt.velX -= pt.x * edgeTension * t
            pt.velX += (1f - pt.x) * farEdgeTension * t
            
            val tOffset = touchOffset
            if (tOffset != null) {
                val ratio = max(0f, 1f - abs(pt.y - tOffset.y) / maxTouchDistance)
                pt.velX += (tOffset.x - pt.x) * touchTension * ratio * t
            }
            if (i > 0) {
                addPointTension(pt, points[i - 1].x, t)
            }
            if (i < l - 1) {
                addPointTension(pt, points[i + 1].x, t)
            }
            pt.velX *= dampingT
        }

        for (i in 0 until l) {
            val pt = points[i]
            pt.x += pt.velX * t
        }
    }

    private fun getTransform(size: Size, margin: Float): Matrix {
        val vertical = side == Side.TOP || side == Side.BOTTOM
        val w = (if (vertical) size.height else size.width) + margin * 2
        val h = (if (vertical) size.width else size.height) + margin * 2

        val mtx = Matrix()
        mtx.translate(-margin, 0f)
        mtx.scale(w, h, 1f)

        when (side) {
            Side.TOP -> {
                mtx.rotateZ(90f)
                mtx.translate(0f, -1f)
            }
            Side.RIGHT -> {
                mtx.rotateZ(180f)
                mtx.translate(-1f, -1f)
            }
            Side.BOTTOM -> {
                mtx.rotateZ(270f)
                mtx.translate(-1f, 0f)
            }
            Side.LEFT -> {
            }
        }
        return mtx
    }

    private fun addPointTension(pt0: FluidPoint, x: Float, t: Float) {
        pt0.velX += (x - pt0.x) * pointTension * t
    }
}

class FluidPoint(
    var x: Float = 0f,
    var y: Float = 0f
) {
    var velX: Float = 0f
    var velY: Float = 0f

    fun toOffset(transform: Matrix? = null): Offset {
        val o = Offset(x, y)
        if (transform == null) return o
        val outOffset = transform.map(o)
        return outOffset
    }
}
