package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import kotlin.math.abs
import kotlin.math.roundToInt

/** Pitch ladder + roll columns for [InclinometerBitmapRenderer]. */
internal class InclinometerDrawHelper(
    private val canvas: Canvas,
    private val sizePx: Int,
) {
    private val kit = InclinometerPaintKit(sizePx)

    fun drawPitch(frame: InclinometerFrame, cx: Float, cy: Float) {
        val pitchHalf = sizePx * 0.32f
        val barH = sizePx * 0.042f
        val gap = sizePx * 0.01f
        val pitchBarW = sizePx * 0.09f
        val leftRail = cx - sizePx * 0.07f
        val rightRail = cx + sizePx * 0.07f
        canvas.drawLine(leftRail, cy - pitchHalf, leftRail, cy + pitchHalf, kit.rail)
        canvas.drawLine(rightRail, cy - pitchHalf, rightRail, cy + pitchHalf, kit.rail)
        listOf(15f, 30f, 45f).forEach { deg ->
            val yOff = (deg / InclinometerColor.MAX_DEG) * pitchHalf
            drawTickPair(leftRail, rightRail, cy - yOff, deg.roundToInt())
            drawTickPair(leftRail, rightRail, cy + yOff, deg.roundToInt())
        }
        for (i in 1..InclinometerSegmentLogic.BARS_PER_SIDE) {
            val yUp = cy - i * (barH + gap)
            val yDown = cy + (i - 1) * (barH + gap) + gap
            kit.roundBar(canvas, cx - pitchBarW / 2f, yUp - barH, cx + pitchBarW / 2f, yUp, kit.dim)
            kit.roundBar(canvas, cx - pitchBarW / 2f, yDown, cx + pitchBarW / 2f, yDown + barH, kit.dim)
        }
        frame.pitchDown.reversed().forEachIndexed { idx, seg ->
            val i = frame.pitchDown.size - idx
            val y = cy - i * (barH + gap)
            kit.fill.color = seg.colorArgb
            kit.roundBar(canvas, cx - pitchBarW / 2f, y - barH, cx + pitchBarW / 2f, y, kit.fill)
        }
        frame.pitchUp.forEachIndexed { idx, seg ->
            val y = cy + idx * (barH + gap) + gap
            kit.fill.color = seg.colorArgb
            kit.roundBar(canvas, cx - pitchBarW / 2f, y, cx + pitchBarW / 2f, y + barH, kit.fill)
        }
        val needleY = cy - (frame.pitchDeg / InclinometerColor.MAX_DEG) * pitchHalf
        kit.pointer(canvas, leftRail - sizePx * 0.02f, needleY, pointingRight = true)
        kit.pointer(canvas, rightRail + sizePx * 0.02f, needleY, pointingRight = false)
        frame.pitchMarkerDeg?.let { m ->
            val y = (m / InclinometerColor.MAX_DEG) * pitchHalf
            canvas.drawLine(leftRail, cy - y, rightRail, cy - y, kit.marker)
            canvas.drawLine(leftRail, cy + y, rightRail, cy + y, kit.marker)
        }
    }

    fun drawRoll(left: Boolean, fill: Float, rollDeg: Float, cx: Float, cy: Float) {
        val pitchHalf = sizePx * 0.32f
        val barH = sizePx * 0.042f
        val gap = sizePx * 0.01f
        val colW = sizePx * 0.11f
        val outerX = if (left) sizePx * 0.06f else sizePx * 0.83f
        val segments = InclinometerSegmentLogic.ROLL_SEGMENTS_PER_SIDE
        val lit = (fill * segments).roundToInt().coerceIn(0, segments)
        val totalH = segments * (barH + gap) - gap
        var y = cy + totalH / 2f - barH
        for (i in 0 until segments) {
            kit.fill.color = if (i < lit) {
                InclinometerColor.argbForAngleMagnitude(
                    abs(rollDeg).coerceAtLeast(1f) * ((i + 1f) / segments),
                )
            } else {
                InclinometerColor.SEGMENT_DIM
            }
            kit.roundBar(canvas, outerX, y, outerX + colW, y + barH, kit.fill)
            y -= barH + gap
        }
        val tickX = if (left) outerX - sizePx * 0.01f else outerX + colW + sizePx * 0.01f
        listOf(0f, 15f, 30f, 45f).forEach { deg ->
            val yOff = (deg / InclinometerColor.MAX_DEG) * pitchHalf
            val dx = if (left) -6f else 6f
            canvas.drawLine(tickX, cy - yOff, tickX + dx, cy - yOff, kit.tick)
            canvas.drawLine(tickX, cy + yOff, tickX + dx, cy + yOff, kit.tick)
        }
        val signed = if (left) -rollDeg else rollDeg
        val needleY = cy - signed.coerceIn(-InclinometerColor.MAX_DEG, InclinometerColor.MAX_DEG) /
            InclinometerColor.MAX_DEG * (totalH / 2f)
        val px = if (left) outerX + colW + sizePx * 0.015f else outerX - sizePx * 0.015f
        kit.pointer(canvas, px, needleY, pointingRight = left)
    }

    fun drawReadouts(pitchDeg: Float, rollDeg: Float, cx: Float) {
        canvas.drawText(kit.formatAngle(pitchDeg), cx, sizePx * 0.11f, kit.text)
        kit.text.textSize = sizePx * 0.07f
        canvas.drawText("R ${kit.formatAngle(rollDeg)}", cx, sizePx * 0.94f, kit.text)
        kit.text.textSize = sizePx * 0.09f
    }

    fun drawAlertFrame() {
        val inset = sizePx * 0.025f
        canvas.drawRect(inset, inset, sizePx - inset, sizePx - inset, kit.alert)
    }

    private fun drawTickPair(leftRail: Float, rightRail: Float, y: Float, label: Int) {
        canvas.drawLine(leftRail - 8f, y, leftRail, y, kit.tick)
        canvas.drawLine(rightRail, y, rightRail + 8f, y, kit.tick)
        kit.tick.textAlign = android.graphics.Paint.Align.RIGHT
        canvas.drawText(label.toString(), leftRail - 10f, y + kit.tick.textSize * 0.35f, kit.tick)
        kit.tick.textAlign = android.graphics.Paint.Align.LEFT
        canvas.drawText(label.toString(), rightRail + 10f, y + kit.tick.textSize * 0.35f, kit.tick)
    }
}
