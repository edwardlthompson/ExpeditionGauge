package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface

/** Cube chrome + TPMS paint helpers for [DriveHudBitmapRenderer]. */
internal class DriveHudCubeDraw(
    internal val canvas: Canvas,
    private val cubePx: Int,
) {
    var pedalThrottlePct: Float? = null
    var pedalLonG: Float = 0f
    var pedalFlashOn: Boolean = true
    var satelliteCount: Int = 0

    fun drawCubeChrome(x: Int, y: Int, size: Int, theme: DriveHudTheme) {
        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = theme.cubeFill }
        val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.cubeStroke
            style = Paint.Style.STROKE
            strokeWidth = size * 0.02f
        }
        val r = size * 0.08f
        val rect = RectF(x.toFloat(), y.toFloat(), (x + size).toFloat(), (y + size).toFloat())
        canvas.drawRoundRect(rect, r, r, fill)
        canvas.drawRoundRect(rect, r, r, stroke)
    }

    fun drawAttitudeBitmap(x: Int, y: Int, size: Int, theme: DriveHudTheme, attitude: android.graphics.Bitmap) {
        drawCubeChrome(x, y, size, theme)
        val inset = (size * 0.06f).toInt().coerceAtLeast(2)
        val inner = (size - inset * 2).coerceAtLeast(32)
        val filter = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(
            attitude,
            null,
            Rect(x + inset, y + inset, x + inset + inner, y + inset + inner),
            filter,
        )
    }

    fun attitudeInnerPx(size: Int): Int {
        val inset = (size * 0.06f).toInt().coerceAtLeast(2)
        return (size - inset * 2).coerceAtLeast(32)
    }

    fun drawTpmsCube(
        x: Int, y: Int, size: Int, theme: DriveHudTheme,
        fl: String, fr: String, rl: String, rr: String,
    ) {
        drawCubeChrome(x, y, size, theme)
        val pad = size * 0.08f
        val gap = size * 0.04f
        val cellW = (size - pad * 2 - gap) / 2f
        val cellH = (size - pad * 2 - gap) / 2f
        val scale = theme.textScale.coerceIn(1f, 1.5f)
        val label = paint(theme.secondaryText, size * 0.065f * scale, bold = true)
        val pressurePaint = paint(theme.primaryText, size * 0.10f * scale, bold = true)
        val tempPaint = paint(theme.secondaryText, size * 0.075f * scale, bold = true)
        fun corner(cx: Float, cy: Float, name: String, reading: String) {
            val rect = RectF(cx, cy, cx + cellW, cy + cellH)
            val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = theme.background }
            val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = theme.cubeStroke
                style = Paint.Style.STROKE
                strokeWidth = size * 0.01f
            }
            canvas.drawRoundRect(rect, cellH * 0.12f, cellH * 0.12f, fill)
            canvas.drawRoundRect(rect, cellH * 0.12f, cellH * 0.12f, stroke)
            val midX = cx + cellW / 2f
            val lines = reading.split('\n', limit = 2)
            val pressure = lines.getOrElse(0) { "--" }
            val temp = lines.getOrNull(1)
            label.textSize = cellH * 0.18f * scale
            canvas.drawText(name, midX, cy + cellH * 0.24f, label)
            pressurePaint.textSize = cellH * (if (temp != null) 0.28f else 0.34f) * scale
            fit(pressurePaint, pressure, cellW * 0.9f)
            canvas.drawText(pressure, midX, cy + cellH * (if (temp != null) 0.52f else 0.68f), pressurePaint)
            if (temp != null) {
                tempPaint.textSize = cellH * 0.22f * scale
                fit(tempPaint, temp, cellW * 0.9f)
                canvas.drawText(temp, midX, cy + cellH * 0.78f, tempPaint)
            }
        }
        val ox = x + pad
        val oy = y + pad
        corner(ox, oy, "FL", fl)
        corner(ox + cellW + gap, oy, "FR", fr)
        corner(ox, oy + cellH + gap, "RL", rl)
        corner(ox + cellW + gap, oy + cellH + gap, "RR", rr)
    }

    internal fun paint(color: Int, textSize: Float, bold: Boolean): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.textSize = textSize
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.MONOSPACE, if (bold) Typeface.BOLD else Typeface.NORMAL)
            isFakeBoldText = bold
        }

    internal fun fit(paint: Paint, text: String, maxWidth: Float) {
        var size = paint.textSize
        while (size > cubePx * 0.04f && paint.measureText(text) > maxWidth) {
            size *= 0.92f
            paint.textSize = size
        }
    }
}
