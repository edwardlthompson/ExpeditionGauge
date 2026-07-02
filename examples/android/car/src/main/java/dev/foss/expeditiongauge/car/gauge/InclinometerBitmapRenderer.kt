package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.min

class InclinometerBitmapRenderer(
    private val sizePx: Int = DEFAULT_SIZE_PX,
) {
    private val bitmap: Bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_TICK
        strokeWidth = sizePx * 0.01f
    }
    private val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.THRESHOLD_MARKER
        strokeWidth = sizePx * 0.015f
        style = Paint.Style.STROKE
    }
    private val alertPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.ALERT_FRAME
        strokeWidth = sizePx * 0.04f
        style = Paint.Style.STROKE
    }

    fun render(
        pitchDeg: Float,
        rollDeg: Float,
        pitchAlert: Boolean = false,
        rollAlert: Boolean = false,
        maxPitchThresholdDeg: Float? = null,
        maxRollThresholdDeg: Float? = null,
    ): Bitmap {
        val frame = InclinometerSegmentLogic.frame(
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            maxPitchThresholdDeg = maxPitchThresholdDeg,
            maxRollThresholdDeg = maxRollThresholdDeg,
        )
        canvas.drawColor(InclinometerColor.BACKGROUND)

        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val barW = sizePx * 0.08f
        val barH = sizePx * 0.055f
        val gap = sizePx * 0.012f
        val centerW = sizePx * 0.14f

        drawPitchTicks(cx, cy, centerW, barH, gap)
        drawRollTicks(cx, cy, sizePx)

        var y = cy - barH / 2f - gap
        frame.pitchDown.reversed().forEach { seg ->
            y -= barH + gap
            fillPaint.color = seg.colorArgb
            canvas.drawRect(cx - barW / 2f, y, cx + barW / 2f, y + barH, fillPaint)
        }

        y = cy + barH / 2f + gap
        frame.pitchUp.forEach { seg ->
            fillPaint.color = seg.colorArgb
            canvas.drawRect(cx - barW / 2f, y, cx + barW / 2f, y + barH, fillPaint)
            y += barH + gap
        }

        val sideW = sizePx * 0.07f
        val sideH = sizePx * 0.045f
        val sideXLeft = cx - centerW - sideW - sizePx * 0.04f
        val sideXRight = cx + centerW + sizePx * 0.04f
        var sideY = cy - (ROLL_SEGMENTS_PER_SIDE * (sideH + gap)) / 2f

        frame.rollLeft.forEachIndexed { index, seg ->
            fillPaint.color = seg.colorArgb
            val rect = RectF(sideXLeft, sideY, sideXLeft + sideW, sideY + sideH)
            canvas.save()
            canvas.rotate(-8f * (index + 1), rect.centerX(), rect.centerY())
            canvas.drawRect(rect, fillPaint)
            canvas.restore()
            sideY += sideH + gap
        }

        sideY = cy - (ROLL_SEGMENTS_PER_SIDE * (sideH + gap)) / 2f
        frame.rollRight.forEachIndexed { index, seg ->
            fillPaint.color = seg.colorArgb
            val rect = RectF(sideXRight, sideY, sideXRight + sideW, sideY + sideH)
            canvas.save()
            canvas.rotate(8f * (index + 1), rect.centerX(), rect.centerY())
            canvas.drawRect(rect, fillPaint)
            canvas.restore()
            sideY += sideH + gap
        }

        frame.pitchMarkerDeg?.let { marker ->
            val offset = (marker / InclinometerColor.MAX_DEG) * (sizePx * 0.35f)
            canvas.drawLine(cx - centerW, cy - offset, cx + centerW, cy - offset, markerPaint)
            canvas.drawLine(cx - centerW, cy + offset, cx + centerW, cy + offset, markerPaint)
        }
        frame.rollMarkerDeg?.let { marker ->
            val offset = (marker / InclinometerColor.MAX_DEG) * (sizePx * 0.28f)
            canvas.drawLine(cx - offset - centerW, cy, cx - offset - centerW + sideW, cy, markerPaint)
            canvas.drawLine(cx + offset + centerW - sideW, cy, cx + offset + centerW, cy, markerPaint)
        }

        if (pitchAlert || rollAlert) {
            val inset = sizePx * 0.03f
            canvas.drawRect(inset, inset, sizePx - inset, sizePx - inset, alertPaint)
        }

        return bitmap
    }

    private fun drawPitchTicks(cx: Float, cy: Float, centerW: Float, barH: Float, gap: Float) {
        val maxOffset = (InclinometerColor.MAX_DEG / InclinometerSegmentLogic.BARS_PER_SIDE) *
            (InclinometerSegmentLogic.BARS_PER_SIDE) / InclinometerColor.MAX_DEG * (sizePx * 0.35f)
        listOf(15f, 30f, 45f).forEach { deg ->
            val offset = (deg / InclinometerColor.MAX_DEG) * min(maxOffset, sizePx * 0.35f)
            canvas.drawLine(cx - centerW - 4f, cy - offset, cx - centerW, cy - offset, tickPaint)
            canvas.drawLine(cx + centerW, cy - offset, cx + centerW + 4f, cy - offset, tickPaint)
            canvas.drawLine(cx - centerW - 4f, cy + offset, cx - centerW, cy + offset, tickPaint)
            canvas.drawLine(cx + centerW, cy + offset, cx + centerW + 4f, cy + offset, tickPaint)
        }
    }

    private fun drawRollTicks(cx: Float, cy: Float, sizePx: Int) {
        val radius = sizePx * 0.38f
        tickPaint.style = Paint.Style.STROKE
        canvas.drawCircle(cx, cy, radius, tickPaint)
        tickPaint.style = Paint.Style.FILL
    }

    companion object {
        const val DEFAULT_SIZE_PX = 256
        private const val ROLL_SEGMENTS_PER_SIDE = InclinometerSegmentLogic.ROLL_SEGMENTS_PER_SIDE
    }
}
