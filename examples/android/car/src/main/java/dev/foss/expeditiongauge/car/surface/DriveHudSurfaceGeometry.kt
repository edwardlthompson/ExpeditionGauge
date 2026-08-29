package dev.foss.expeditiongauge.car.surface

import android.graphics.Bitmap
import android.graphics.Rect
import dev.foss.expeditiongauge.car.HudStripOrientation

/** Fit-rect + attitude hit-test helpers for [DriveHudSurfacePainter]. */
internal object DriveHudSurfaceGeometry {
    fun fitRect(srcW: Int, srcH: Int, bounds: Rect): Rect {
        if (srcW <= 0 || srcH <= 0 || bounds.isEmpty) return Rect(bounds)
        val scale = minOf(
            bounds.width().toFloat() / srcW,
            bounds.height().toFloat() / srcH,
        )
        val w = (srcW * scale).toInt().coerceAtLeast(1)
        val h = (srcH * scale).toInt().coerceAtLeast(1)
        val left = bounds.left + (bounds.width() - w) / 2
        val top = bounds.top + (bounds.height() - h) / 2
        return Rect(left, top, left + w, top + h)
    }

    fun isAttitudeCubeTap(
        x: Float,
        y: Float,
        bounds: Rect,
        orientation: HudStripOrientation,
        strip: Bitmap?,
    ): Boolean {
        if (bounds.width() <= 0 || bounds.height() <= 0) return false
        if (x < bounds.left || x > bounds.right) return false
        if (y < bounds.top || y > bounds.bottom) return false
        return when (orientation) {
            HudStripOrientation.COLUMN -> y < bounds.top + bounds.height() / 2f
            HudStripOrientation.ROW -> {
                if (strip != null && strip.width > 0 && strip.height > 0) {
                    val dst = fitRect(strip.width, strip.height, bounds)
                    val cubeSrcH = minOf(strip.height, strip.width / 3)
                    val cubeDstH = (dst.height() * cubeSrcH) / strip.height
                    if (y >= dst.top + cubeDstH) return false
                    x < dst.left + dst.width() / 3f
                } else {
                    x < bounds.left + bounds.width() / 3f
                }
            }
        }
    }

    fun isDtcFooterTap(
        x: Float,
        y: Float,
        bounds: Rect,
        orientation: HudStripOrientation,
        strip: Bitmap?,
    ): Boolean {
        if (orientation != HudStripOrientation.ROW) return false
        if (bounds.width() <= 0 || bounds.height() <= 0) return false
        if (strip == null || strip.width <= 0 || strip.height <= 0) return false
        val dst = fitRect(strip.width, strip.height, bounds)
        if (x < dst.left || x > dst.right || y < dst.top || y > dst.bottom) return false
        val cubeSrcH = minOf(strip.height, strip.width / 3)
        val cubeDstH = (dst.height() * cubeSrcH) / strip.height
        return y >= dst.top + cubeDstH
    }

    fun isTelemetryCubeTap(
        x: Float,
        y: Float,
        bounds: Rect,
        orientation: HudStripOrientation,
        strip: Bitmap?,
    ): Boolean {
        if (bounds.width() <= 0 || bounds.height() <= 0) return false
        if (x < bounds.left || x > bounds.right) return false
        if (y < bounds.top || y > bounds.bottom) return false
        return when (orientation) {
            HudStripOrientation.COLUMN -> y >= bounds.top + bounds.height() / 2f
            HudStripOrientation.ROW -> {
                if (strip != null && strip.width > 0 && strip.height > 0) {
                    val dst = fitRect(strip.width, strip.height, bounds)
                    val cubeSrcH = minOf(strip.height, strip.width / 3)
                    val cubeDstH = (dst.height() * cubeSrcH) / strip.height
                    if (y >= dst.top + cubeDstH) return false
                    val third = dst.width() / 3f
                    x >= dst.left + third && x < dst.left + third * 2f
                } else {
                    val third = bounds.width() / 3f
                    x >= bounds.left + third && x < bounds.left + third * 2f
                }
            }
        }
    }
}
