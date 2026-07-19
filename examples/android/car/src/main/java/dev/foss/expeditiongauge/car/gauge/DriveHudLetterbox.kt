package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

/** Pane fallback: center a 3×1 strip inside a square so host crop keeps all cubes. */
object DriveHudLetterbox {
    fun toSquare(strip: Bitmap, darkBackground: Boolean = true): Bitmap {
        val size = strip.width.coerceAtLeast(1)
        val out = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val theme = DriveHudTheme.forDarkMode(darkBackground)
        canvas.drawColor(theme.background)
        val top = (size - strip.height) / 2
        canvas.drawBitmap(strip, null, Rect(0, top, size, top + strip.height), null)
        return out
    }
}
