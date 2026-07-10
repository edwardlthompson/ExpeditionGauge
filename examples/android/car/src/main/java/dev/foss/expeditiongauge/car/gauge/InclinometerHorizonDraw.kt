package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

/** Aviation-style artificial horizon (attitude indicator). */
internal class InclinometerHorizonDraw(
    private val canvas: Canvas,
    private val sizePx: Int,
) {
    private val kit = InclinometerPaintKit(sizePx)
    private val sky = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF1A5C9E.toInt() }
    private val ground = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF6B4A2A.toInt() }
    private val wing = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.READOUT
        strokeWidth = sizePx * 0.02f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val bank = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_TICK
        strokeWidth = sizePx * 0.012f
        style = Paint.Style.STROKE
    }

    fun draw(
        pitchDeg: Float,
        rollDeg: Float,
        alert: Boolean,
        labelPitchDeg: Float = pitchDeg,
        labelRollDeg: Float = rollDeg,
    ) {
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val r = sizePx * 0.42f
        val pitchN = (pitchDeg / InclinometerColor.MAX_DEG).coerceIn(-1f, 1f)
        val rollRad = Math.toRadians(rollDeg.toDouble()).toFloat()
        val pitchShift = pitchN * r * 0.55f

        canvas.save()
        canvas.clipPath(Path().apply { addCircle(cx, cy, r, Path.Direction.CW) })
        canvas.translate(cx, cy)
        canvas.rotate(-rollDeg)
        canvas.translate(0f, pitchShift)
        canvas.drawRect(-r * 2f, -r * 2f, r * 2f, 0f, sky)
        canvas.drawRect(-r * 2f, 0f, r * 2f, r * 2f, ground)
        canvas.drawLine(-r * 2f, 0f, r * 2f, 0f, kit.rail)
        listOf(-30f, -15f, 15f, 30f).forEach { deg ->
            val y = -(deg / InclinometerColor.MAX_DEG) * r * 0.55f
            val half = r * 0.18f
            canvas.drawLine(-half, y, half, y, kit.tick)
        }
        canvas.restore()

        canvas.drawCircle(cx, cy, r, bank)
        drawBankTicks(cx, cy, r)
        canvas.drawLine(cx - r * 0.35f, cy, cx - r * 0.08f, cy, wing)
        canvas.drawLine(cx + r * 0.08f, cy, cx + r * 0.35f, cy, wing)
        canvas.drawCircle(cx, cy, sizePx * 0.018f, kit.pointer)
        canvas.drawLine(cx, cy, cx, cy + r * 0.12f, wing)

        val px = cx + r * sin(rollRad)
        val py = cy - r * cos(rollRad)
        canvas.drawCircle(px, py, sizePx * 0.02f, kit.pointer)

        drawCornerReadouts(labelPitchDeg, labelRollDeg)
        if (alert) {
            canvas.drawRect(
                sizePx * 0.025f, sizePx * 0.025f,
                sizePx * 0.975f, sizePx * 0.975f,
                kit.alert,
            )
        }
    }

    /** P top-left / R bottom-right — single-line labels in cube corner wedges. */
    private fun drawCornerReadouts(pitchDeg: Float, rollDeg: Float) {
        val inset = sizePx * 0.028f
        kit.text.textSize = sizePx * 0.05f
        kit.text.textAlign = Paint.Align.LEFT
        canvas.drawText("P ${kit.formatAngle(pitchDeg)}", inset, inset - kit.text.fontMetrics.ascent, kit.text)
        kit.text.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            "R ${kit.formatAngle(rollDeg)}",
            sizePx - inset,
            sizePx - inset - kit.text.fontMetrics.descent,
            kit.text,
        )
        kit.text.textAlign = Paint.Align.CENTER
    }

    private fun drawBankTicks(cx: Float, cy: Float, r: Float) {
        listOf(-60f, -45f, -30f, -15f, 0f, 15f, 30f, 45f, 60f).forEach { deg ->
            val rad = Math.toRadians(deg.toDouble())
            val outer = r
            val inner = r * if (deg % 30f == 0f) 0.88f else 0.92f
            val ox = cx + outer * sin(rad).toFloat()
            val oy = cy - outer * cos(rad).toFloat()
            val ix = cx + inner * sin(rad).toFloat()
            val iy = cy - inner * cos(rad).toFloat()
            canvas.drawLine(ix, iy, ox, oy, bank)
        }
    }
}
