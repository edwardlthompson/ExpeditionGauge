package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

/**
 * Glanceable Telemetry tile bitmap for Android Auto GridItem images.
 * Large speed, then heading and altitude — denser than host secondary text alone.
 */
class TelemetryTileBitmapRenderer(
    private val sizePx: Int = InclinometerBitmapRenderer.DEFAULT_SIZE_PX,
) {
    private val bitmap: Bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val speedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.READOUT
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        isFakeBoldText = true
    }
    private val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_TICK
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }
    private val accent = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.argbForNormalized(0.15f)
        strokeWidth = sizePx * 0.012f
        style = Paint.Style.STROKE
    }

    fun render(
        speedLabel: String,
        headingLabel: String,
        altLabel: String,
        darkBackground: Boolean = true,
    ): Bitmap {
        canvas.drawColor(
            if (darkBackground) InclinometerColor.BACKGROUND else InclinometerColor.BACKGROUND_LIGHT,
        )
        val inset = sizePx * 0.08f
        canvas.drawRoundRect(
            inset,
            inset,
            sizePx - inset,
            sizePx - inset,
            sizePx * 0.06f,
            sizePx * 0.06f,
            accent,
        )

        speedPaint.textSize = sizePx * 0.22f
        fitText(speedPaint, speedLabel, sizePx * 0.84f)
        val cx = sizePx / 2f
        val speedY = sizePx * 0.42f - (speedPaint.descent() + speedPaint.ascent()) / 2f
        canvas.drawText(speedLabel, cx, speedY, speedPaint)

        subPaint.textSize = sizePx * 0.11f
        fitText(subPaint, headingLabel, sizePx * 0.84f)
        val hdgY = sizePx * 0.62f - (subPaint.descent() + subPaint.ascent()) / 2f
        canvas.drawText(headingLabel, cx, hdgY, subPaint)

        subPaint.textSize = sizePx * 0.095f
        fitText(subPaint, altLabel, sizePx * 0.84f)
        val altY = sizePx * 0.78f - (subPaint.descent() + subPaint.ascent()) / 2f
        canvas.drawText(altLabel, cx, altY, subPaint)

        return bitmap
    }

    private fun fitText(paint: Paint, text: String, maxWidth: Float) {
        var size = paint.textSize
        while (size > sizePx * 0.05f && paint.measureText(text) > maxWidth) {
            size *= 0.92f
            paint.textSize = size
        }
    }
}
