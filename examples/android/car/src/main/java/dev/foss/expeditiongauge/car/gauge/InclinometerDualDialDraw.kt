package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin

/** Twin dials: pitch (left) and roll (right) — common offroad HUD layout. */
internal class InclinometerDualDialDraw(
    private val canvas: Canvas,
    private val sizePx: Int,
) {
    private val kit = InclinometerPaintKit(sizePx)
    private val arc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = sizePx * 0.045f
        strokeCap = Paint.Cap.ROUND
    }
    private val track = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SEGMENT_DIM
        style = Paint.Style.STROKE
        strokeWidth = sizePx * 0.045f
    }
    private val needle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_RAIL
        strokeWidth = sizePx * 0.02f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    fun draw(
        pitchDeg: Float,
        rollDeg: Float,
        alert: Boolean,
        yawDeg: Float? = null,
        latG: Float? = null,
        lonG: Float? = null,
    ) {
        val cy = sizePx / 2f
        val r = sizePx * 0.22f
        drawDial(sizePx * 0.28f, cy, r, pitchDeg)
        drawDial(sizePx * 0.72f, cy, r, rollDeg)
        InclinometerCornerReadouts.draw(canvas, sizePx, kit, pitchDeg, rollDeg, yawDeg, latG, lonG)
        if (alert) {
            canvas.drawRect(
                sizePx * 0.025f, sizePx * 0.025f,
                sizePx * 0.975f, sizePx * 0.975f,
                kit.alert,
            )
        }
    }

    private fun drawDial(cx: Float, cy: Float, r: Float, deg: Float) {
        val oval = RectF(cx - r, cy - r, cx + r, cy + r)
        canvas.drawArc(oval, 135f, 270f, false, track)
        val n = (deg / InclinometerColor.MAX_DEG).coerceIn(-1f, 1f)
        val sweep = n * 135f
        arc.color = InclinometerColor.argbForAngleMagnitude(kotlin.math.abs(deg))
        canvas.drawArc(oval, 270f, sweep, false, arc)
        val needleRad = Math.toRadians(270.0 + sweep)
        val nx = cx + r * 0.75f * cos(needleRad).toFloat()
        val ny = cy + r * 0.75f * sin(needleRad).toFloat()
        canvas.drawLine(cx, cy, nx, ny, needle)
        canvas.drawCircle(cx, cy, sizePx * 0.02f, kit.pointer)
    }
}
