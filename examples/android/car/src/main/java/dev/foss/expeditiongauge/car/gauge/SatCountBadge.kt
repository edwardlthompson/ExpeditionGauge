package dev.foss.expeditiongauge.car.gauge

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

/** Notification bubble on the GPS satellite glyph. */
object SatCountBadge {
    fun label(count: Int): String = count.coerceIn(0, 99).toString()

    fun draw(
        canvas: Canvas,
        iconCx: Float,
        iconCy: Float,
        iconSize: Float,
        count: Int,
        bubbleColor: Int,
    ) {
        val text = label(count)
        val r = (iconSize * 0.30f).coerceAtLeast(6f)
        val bx = iconCx + iconSize * 0.34f
        val by = iconCy - iconSize * 0.34f
        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bubbleColor
            style = Paint.Style.FILL
        }
        canvas.drawCircle(bx, by, r, fill)
        val tp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = if (text.length > 1) r * 1.15f else r * 1.35f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val fm = tp.fontMetrics
        canvas.drawText(text, bx, by - (fm.ascent + fm.descent) / 2f, tp)
    }
}
