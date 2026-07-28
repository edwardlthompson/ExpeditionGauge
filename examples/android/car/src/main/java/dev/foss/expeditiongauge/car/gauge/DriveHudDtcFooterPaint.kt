package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

/** Bold-red single-line DTC footer for ROW Drive HUD (band always reserved). */
internal object DriveHudDtcFooterPaint {
    fun draw(
        canvas: Canvas,
        line: String,
        theme: DriveHudTheme,
        cubePx: Int,
        footerPx: Int,
        stripW: Int,
        /** Same gap used for cube layout — under-cube pad is part of the visual band. */
        cubeGapPx: Int = (cubePx * 0.04f).toInt().coerceAtLeast(2),
    ) {
        if (footerPx <= 0 || line.isBlank()) return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            color = theme.alertText
        }
        val padX = (cubePx * 0.04f).coerceAtLeast(8f)
        val maxW = stripW - padX * 2f
        // Leave ~30% of the visual band as vertical padding so type can sit centered.
        val visualTop = cubePx - cubeGapPx / 2f
        val visualBottom = (cubePx + footerPx).toFloat()
        val bandH = (visualBottom - visualTop).coerceAtLeast(1f)
        val maxSize = (bandH * 0.70f).coerceIn(28f, 56f)
        val minSize = (bandH * 0.38f).coerceIn(16f, 28f)
        paint.textSize = fitTextSize(maxW, minSize, maxSize) { size ->
            paint.textSize = size
            paint.measureText(line)
        }
        val text = if (paint.measureText(line) <= maxW) {
            line
        } else {
            truncateEllipsis(line, maxW) { paint.measureText(it) }
        }
        if (text.isEmpty()) return
        val fm = paint.fontMetrics
        // Full gap (cube bottoms → strip bottom). Downward bias (~17% of band).
        val centerY = (visualTop + visualBottom) / 2f + bandH * 0.17f
        val baseline = centerY - (fm.ascent + fm.descent) / 2f
        canvas.drawText(text, padX, baseline, paint)
    }

    /**
     * Largest size in [[minSize], [maxSize]] where [widthAt] ≤ [maxWidth].
     * Expands for short lines; shrinks for long ones.
     */
    fun fitTextSize(
        maxWidth: Float,
        minSize: Float,
        maxSize: Float,
        widthAt: (Float) -> Float,
    ): Float {
        if (maxWidth <= 0f) return minSize
        val loBound = minOf(minSize, maxSize)
        val hiBound = maxOf(minSize, maxSize)
        if (widthAt(hiBound) <= maxWidth) return hiBound
        if (widthAt(loBound) > maxWidth) return loBound
        var lo = loBound
        var hi = hiBound
        repeat(14) {
            val mid = (lo + hi) / 2f
            if (widthAt(mid) <= maxWidth) lo = mid else hi = mid
        }
        return lo
    }

    fun truncateEllipsis(
        text: String,
        maxWidth: Float,
        measure: (String) -> Float,
    ): String {
        if (maxWidth <= 0f || text.isEmpty()) return ""
        if (measure(text) <= maxWidth) return text
        val ellipsis = "…"
        if (measure(ellipsis) > maxWidth) return ""
        var lo = 0
        var hi = text.length
        while (lo < hi) {
            val mid = (lo + hi + 1) / 2
            if (measure(text.take(mid) + ellipsis) <= maxWidth) lo = mid else hi = mid - 1
        }
        return if (lo <= 0) ellipsis else text.take(lo) + ellipsis
    }
}
