package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

/** Spirit-level tubes: vertical = pitch, horizontal = roll. */
internal class InclinometerBubbleDraw(
    private val canvas: Canvas,
    private val sizePx: Int,
) {
    private val kit = InclinometerPaintKit(sizePx)
    private val tube = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SEGMENT_DIM
        style = Paint.Style.FILL
    }
    private val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_RAIL
        style = Paint.Style.STROKE
        strokeWidth = sizePx * 0.01f
    }
    private val bubble = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val zero = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_TICK
        strokeWidth = sizePx * 0.012f
    }

    fun draw(
        pitchDeg: Float,
        rollDeg: Float,
        alert: Boolean,
        yawDeg: Float? = null,
        latG: Float? = null,
        lonG: Float? = null,
    ) {
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        drawVerticalTube(cx, cy, pitchDeg)
        drawHorizontalTube(cx, cy, rollDeg)
        InclinometerCornerReadouts.draw(canvas, sizePx, kit, pitchDeg, rollDeg, yawDeg, latG, lonG)
        if (alert) {
            canvas.drawRect(
                sizePx * 0.025f, sizePx * 0.025f,
                sizePx * 0.975f, sizePx * 0.975f,
                kit.alert,
            )
        }
    }

    private fun drawVerticalTube(cx: Float, cy: Float, pitchDeg: Float) {
        val w = sizePx * 0.12f
        val h = sizePx * 0.55f
        val left = cx - w / 2f
        val top = cy - h / 2f
        val rect = RectF(left, top, left + w, top + h)
        canvas.drawRoundRect(rect, w / 2f, w / 2f, tube)
        canvas.drawRoundRect(rect, w / 2f, w / 2f, outline)
        canvas.drawLine(left, cy, left + w, cy, zero)
        val n = (pitchDeg / InclinometerColor.MAX_DEG).coerceIn(-1f, 1f)
        // Nose up (neg pitch in our braking convention) → bubble toward top
        val by = cy + n * (h / 2f - w * 0.55f)
        bubble.color = InclinometerColor.argbForAngleMagnitude(kotlin.math.abs(pitchDeg))
        canvas.drawCircle(cx, by, w * 0.38f, bubble)
    }

    private fun drawHorizontalTube(cx: Float, cy: Float, rollDeg: Float) {
        val h = sizePx * 0.12f
        val w = sizePx * 0.55f
        val left = cx - w / 2f
        val y = cy + sizePx * 0.28f
        val hRect = RectF(left, y - h / 2f, left + w, y + h / 2f)
        canvas.drawRoundRect(hRect, h / 2f, h / 2f, tube)
        canvas.drawRoundRect(hRect, h / 2f, h / 2f, outline)
        canvas.drawLine(cx, y - h / 2f, cx, y + h / 2f, zero)
        val n = (rollDeg / InclinometerColor.MAX_DEG).coerceIn(-1f, 1f)
        val bx = cx + n * (w / 2f - h * 0.55f)
        bubble.color = InclinometerColor.argbForAngleMagnitude(kotlin.math.abs(rollDeg))
        canvas.drawCircle(bx, y, h * 0.38f, bubble)
    }
}
