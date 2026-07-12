package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import kotlin.math.hypot
import kotlin.math.roundToInt

/**
 * Four-corner inclinometer readouts (never merge axes):
 * TL pitch · TR roll · BL yaw · BR g-force.
 * Monospace autofit stays outside the attitude dial.
 */
internal object InclinometerCornerReadouts {
    private const val WORST_PITCH = "P −180°"
    private const val WORST_ROLL = "R +180°"
    private const val WORST_YAW = "Y −180°"
    private const val WORST_G = "↔ −9.9"

    fun draw(
        canvas: Canvas,
        sizePx: Int,
        kit: InclinometerPaintKit,
        pitchDeg: Float,
        rollDeg: Float,
        yawDeg: Float?,
        latG: Float?,
        lonG: Float?,
    ) {
        val inset = sizePx * 0.03f
        val maxPx = sizePx * 0.072f
        val minPx = sizePx * 0.034f
        val dialClearance = sizePx * 0.03f
        val quadrantWidth = (sizePx / 2f) - inset - dialClearance
        val paint = kit.text
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        paint.isFakeBoldText = false

        val pitchLabel = "P ${kit.formatAngle(pitchDeg)}"
        val rollLabel = "R ${kit.formatAngle(rollDeg)}"
        val yawLabel = yawDeg?.let { "Y ${kit.formatAngle(it)}" } ?: "Y —"
        val gLat = latG ?: 0f
        val gLon = lonG ?: 0f
        val gTop = if (latG == null && lonG == null) "G —" else "↔ ${formatSignedG(gLat)}"
        val gBottom = if (latG == null && lonG == null) null else "↕ ${formatSignedG(gLon)}"

        fun wider(a: String, b: String): String {
            paint.textSize = maxPx
            return if (paint.measureText(a) >= paint.measureText(b)) a else b
        }
        val measureStrings = listOf(
            wider(pitchLabel, WORST_PITCH),
            wider(rollLabel, WORST_ROLL),
            wider(yawLabel, WORST_YAW),
            wider(gTop, WORST_G),
        ) + listOfNotNull(gBottom)

        var lo = minPx
        var hi = maxPx
        var best = minPx
        repeat(14) {
            val mid = (lo + hi) / 2f
            paint.textSize = mid
            val fits = measureStrings.all { paint.measureText(it) <= quadrantWidth } &&
                (gBottom == null || mid * 1.15f * 2f <= (sizePx / 2f) - inset)
            if (fits) {
                best = mid
                lo = mid
            } else {
                hi = mid
            }
        }
        paint.textSize = best

        val topY = inset - paint.fontMetrics.ascent
        val bottomY = sizePx - inset - paint.fontMetrics.descent

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(pitchLabel, inset, topY, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(rollLabel, sizePx - inset, topY, paint)

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(yawLabel, inset, bottomY, paint)

        paint.textAlign = Paint.Align.RIGHT
        if (gBottom == null) {
            canvas.drawText(gTop, sizePx - inset, bottomY, paint)
        } else {
            val lineH = paint.textSize * 1.15f
            canvas.drawText(gTop, sizePx - inset, bottomY - lineH, paint)
            canvas.drawText(gBottom, sizePx - inset, bottomY, paint)
        }

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = sizePx * 0.09f
        paint.typeface = Typeface.DEFAULT
    }

    fun formatG(latG: Float?, lonG: Float?): String {
        if (latG == null && lonG == null) return "G —"
        val mag = hypot((latG ?: 0f).toDouble(), (lonG ?: 0f).toDouble()).toFloat()
        return "G ${formatSignedG(mag)}"
    }

    private fun formatSignedG(value: Float): String {
        val rounded = (kotlin.math.abs(value) * 10f).roundToInt() / 10f
        val sign = if (value < 0f) "−" else ""
        return "$sign${"%.1f".format(rounded)}"
    }
}
