package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.roundToInt

/** Shared paints + small primitives for inclinometer bitmap drawing. */
internal class InclinometerPaintKit(private val sizePx: Int) {
    val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    val dim = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = InclinometerColor.SEGMENT_DIM }
    val tick = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_TICK
        strokeWidth = sizePx * 0.008f
        textSize = sizePx * 0.055f
    }
    val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.READOUT
        textAlign = Paint.Align.CENTER
        textSize = sizePx * 0.09f
        isFakeBoldText = true
    }
    val pointer = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.POINTER
        style = Paint.Style.FILL
    }
    val marker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.THRESHOLD_MARKER
        strokeWidth = sizePx * 0.012f
        style = Paint.Style.STROKE
    }
    val rail = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_RAIL
        strokeWidth = sizePx * 0.01f
        style = Paint.Style.STROKE
    }
    val alert = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.ALERT_FRAME
        strokeWidth = sizePx * 0.035f
        style = Paint.Style.STROKE
    }

    fun roundBar(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        val r = (bottom - top) / 3f
        canvas.drawRoundRect(RectF(left, top, right, bottom), r, r, paint)
    }

    fun pointer(canvas: Canvas, x: Float, y: Float, pointingRight: Boolean) {
        val h = sizePx * 0.035f
        val w = sizePx * 0.04f
        val path = Path()
        if (pointingRight) {
            path.moveTo(x, y)
            path.lineTo(x - w, y - h)
            path.lineTo(x - w, y + h)
        } else {
            path.moveTo(x, y)
            path.lineTo(x + w, y - h)
            path.lineTo(x + w, y + h)
        }
        path.close()
        canvas.drawPath(path, pointer)
    }

    fun formatAngle(deg: Float): String {
        val rounded = (deg * 10f).roundToInt() / 10f
        val sign = when {
            rounded > 0f -> "+"
            rounded < 0f -> "−"
            else -> ""
        }
        return "$sign${"%.1f".format(abs(rounded))}°"
    }
}
