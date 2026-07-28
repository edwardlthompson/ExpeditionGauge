package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

/** Vector-ish link glyphs painted into the AA telemetry cube (no letters). */
internal object DriveHudLinkIcons {
    fun drawGps(canvas: Canvas, cx: Float, cy: Float, size: Float, paint: Paint) {
        val r = size * 0.22f
        canvas.drawCircle(cx, cy - size * 0.08f, r, paint)
        val path = Path().apply {
            moveTo(cx, cy - size * 0.08f + r * 0.2f)
            lineTo(cx - size * 0.18f, cy + size * 0.32f)
            lineTo(cx + size * 0.18f, cy + size * 0.32f)
            close()
        }
        canvas.drawPath(path, paint)
        // Dish arcs
        val stroke = Paint(paint).apply {
            style = Paint.Style.STROKE
            strokeWidth = size * 0.08f
        }
        canvas.drawArc(
            RectF(cx - size * 0.38f, cy - size * 0.42f, cx + size * 0.38f, cy + size * 0.1f),
            200f, 140f, false, stroke,
        )
    }

    fun drawObd(canvas: Canvas, cx: Float, cy: Float, size: Float, paint: Paint) {
        val stroke = Paint(paint).apply {
            style = Paint.Style.STROKE
            strokeWidth = size * 0.1f
            strokeCap = Paint.Cap.ROUND
        }
        val oval = RectF(cx - size * 0.38f, cy - size * 0.28f, cx + size * 0.38f, cy + size * 0.48f)
        canvas.drawArc(oval, 200f, 140f, false, stroke)
        canvas.drawCircle(cx, cy + size * 0.12f, size * 0.08f, paint)
        canvas.drawLine(cx, cy + size * 0.12f, cx + size * 0.22f, cy - size * 0.18f, stroke)
    }

    fun drawTpms(canvas: Canvas, cx: Float, cy: Float, size: Float, paint: Paint) {
        val stroke = Paint(paint).apply {
            style = Paint.Style.STROKE
            strokeWidth = size * 0.1f
        }
        canvas.drawCircle(cx, cy, size * 0.36f, stroke)
        canvas.drawCircle(cx, cy, size * 0.18f, stroke)
        // Valve nub
        canvas.drawRect(cx - size * 0.05f, cy - size * 0.42f, cx + size * 0.05f, cy - size * 0.3f, paint)
    }

    fun drawImu(canvas: Canvas, cx: Float, cy: Float, size: Float, paint: Paint) {
        val r = size * 0.1f
        val d = size * 0.22f
        canvas.drawCircle(cx - d, cy - d, r, paint)
        canvas.drawCircle(cx + d, cy - d, r, paint)
        canvas.drawCircle(cx - d, cy + d, r, paint)
        canvas.drawCircle(cx + d, cy + d, r, paint)
        val stroke = Paint(paint).apply {
            style = Paint.Style.STROKE
            strokeWidth = size * 0.07f
        }
        canvas.drawLine(cx - d, cy - d, cx + d, cy + d, stroke)
        canvas.drawLine(cx + d, cy - d, cx - d, cy + d, stroke)
    }
}
