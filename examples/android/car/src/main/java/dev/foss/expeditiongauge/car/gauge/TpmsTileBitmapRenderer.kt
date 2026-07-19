package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface

/**
 * Glanceable TPMS tile: 2×2 corner pressures painted into the CarIcon bitmap.
 */
class TpmsTileBitmapRenderer(
    private val sizePx: Int = InclinometerBitmapRenderer.DEFAULT_SIZE_PX,
) {
    private val bitmap: Bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val cell = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SEGMENT_DIM
        style = Paint.Style.FILL
    }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_RAIL
        style = Paint.Style.STROKE
        strokeWidth = sizePx * 0.01f
    }
    private val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.SCALE_TICK
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = sizePx * 0.08f
    }
    private val value = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = InclinometerColor.READOUT
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textSize = sizePx * 0.12f
        isFakeBoldText = true
    }

    fun render(
        fl: String,
        fr: String,
        rl: String,
        rr: String,
        darkBackground: Boolean = true,
    ): Bitmap {
        canvas.drawColor(
            if (darkBackground) InclinometerColor.BACKGROUND else InclinometerColor.BACKGROUND_LIGHT,
        )
        val gap = sizePx * 0.04f
        val inset = sizePx * 0.06f
        val cellW = (sizePx - inset * 2f - gap) / 2f
        val cellH = (sizePx - inset * 2f - gap) / 2f
        drawCorner(inset, inset, cellW, cellH, "FL", fl)
        drawCorner(inset + cellW + gap, inset, cellW, cellH, "FR", fr)
        drawCorner(inset, inset + cellH + gap, cellW, cellH, "RL", rl)
        drawCorner(inset + cellW + gap, inset + cellH + gap, cellW, cellH, "RR", rr)
        return bitmap
    }

    private fun drawCorner(x: Float, y: Float, w: Float, h: Float, corner: String, reading: String) {
        val rect = RectF(x, y, x + w, y + h)
        val r = h * 0.12f
        canvas.drawRoundRect(rect, r, r, cell)
        canvas.drawRoundRect(rect, r, r, stroke)
        val cx = x + w / 2f
        label.textSize = h * 0.22f
        canvas.drawText(corner, cx, y + h * 0.32f, label)
        value.textSize = h * 0.34f
        fitText(value, reading, w * 0.88f)
        canvas.drawText(reading, cx, y + h * 0.72f, value)
    }

    private fun fitText(paint: Paint, text: String, maxWidth: Float) {
        var size = paint.textSize
        while (size > sizePx * 0.04f && paint.measureText(text) > maxWidth) {
            size *= 0.92f
            paint.textSize = size
        }
    }
}
