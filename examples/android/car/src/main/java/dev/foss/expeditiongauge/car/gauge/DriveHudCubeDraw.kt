package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface

/** Cube chrome + telemetry/TPMS paint helpers for [DriveHudBitmapRenderer]. */
internal class DriveHudCubeDraw(
    private val canvas: Canvas,
    private val cubePx: Int,
) {
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

    fun drawTelemetryCube(
        x: Int, y: Int, size: Int, theme: DriveHudTheme,
        speed: String, heading: String, alt: String, coords: String,
    ) {
        drawCubeChrome(x, y, size, theme)
        val primary = paint(theme.primaryText, size * 0.14f, bold = true)
        val secondary = paint(theme.secondaryText, size * 0.08f, bold = true)
        val tertiary = paint(theme.secondaryText, size * 0.06f, bold = true)
        val cx = x + size / 2f
        val coordLines = coords.lines().filter { it.isNotBlank() }
        val stacked = coordLines.size >= 2
        fit(primary, speed, size * 0.88f)
        canvas.drawText(speed, cx, y + size * (if (stacked) 0.28f else 0.34f), primary)
        fit(secondary, heading, size * 0.88f)
        canvas.drawText(heading, cx, y + size * (if (stacked) 0.44f else 0.52f), secondary)
        fit(secondary, alt, size * 0.88f)
        canvas.drawText(alt, cx, y + size * (if (stacked) 0.58f else 0.68f), secondary)
        if (coordLines.isNotEmpty()) {
            val lineH = size * 0.085f
            val baseY = y + size * (if (stacked) 0.74f else 0.86f)
            coordLines.take(2).forEachIndexed { i, line ->
                fit(tertiary, line, size * 0.92f)
                canvas.drawText(line, cx, baseY + i * lineH, tertiary)
            }
        }
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
        val label = paint(theme.secondaryText, size * 0.065f, bold = true)
        val pressurePaint = paint(theme.primaryText, size * 0.10f, bold = true)
        val tempPaint = paint(theme.secondaryText, size * 0.075f, bold = true)
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
            label.textSize = cellH * 0.18f
            canvas.drawText(name, midX, cy + cellH * 0.24f, label)
            pressurePaint.textSize = cellH * (if (temp != null) 0.28f else 0.34f)
            fit(pressurePaint, pressure, cellW * 0.9f)
            canvas.drawText(pressure, midX, cy + cellH * (if (temp != null) 0.52f else 0.68f), pressurePaint)
            if (temp != null) {
                tempPaint.textSize = cellH * 0.22f
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

    private fun paint(color: Int, textSize: Float, bold: Boolean): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.textSize = textSize
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.MONOSPACE, if (bold) Typeface.BOLD else Typeface.NORMAL)
            isFakeBoldText = bold
        }

    private fun fit(paint: Paint, text: String, maxWidth: Float) {
        var size = paint.textSize
        while (size > cubePx * 0.04f && paint.measureText(text) > maxWidth) {
            size *= 0.92f
            paint.textSize = size
        }
    }
}
