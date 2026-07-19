package dev.foss.expeditiongauge.car.surface

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.Surface
import androidx.car.app.SurfaceContainer
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.gauge.DriveHudTheme
import java.util.concurrent.atomic.AtomicReference

/**
 * Paints a native 3×1 Drive HUD bitmap into the AA host [Surface],
 * scale-to-fit centered in the visible rect.
 */
class DriveHudSurfacePainter {
    private val hudBitmap = AtomicReference<Bitmap?>(null)
    private val darkMode = AtomicReference(true)
    private val bitmapPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    @Volatile private var surface: Surface? = null
    @Volatile private var surfaceW: Int = 0
    @Volatile private var surfaceH: Int = 0
    @Volatile private var visible = Rect()
    @Volatile private var lastDrawMs: Long = 0L
    @Volatile private var lastFrame: Bitmap? = null
    /** Fired when visible layout changes so the screen can re-render at matching px. */
    var onLayoutChanged: (() -> Unit)? = null

    fun setHudBitmap(bitmap: Bitmap?, darkBackground: Boolean) {
        hudBitmap.set(bitmap)
        darkMode.set(darkBackground)
    }

    /** Cube edge matching current visible area (1:1 paint, no upscale). */
    fun targetCubePx(): Int = AaDisplaySpec.surfaceCubePx(visible.width(), visible.height())

    /** True when [x],[y] (surface coords) fall in the left third (attitude cube). */
    fun isAttitudeCubeTap(x: Float, y: Float): Boolean {
        val bounds = visible
        if (bounds.width() <= 0 || bounds.height() <= 0) return false
        if (y < bounds.top || y > bounds.bottom) return false
        val third = bounds.left + bounds.width() / 3f
        return x >= bounds.left && x < third
    }

    fun onSurfaceAvailable(container: SurfaceContainer) {
        surface = container.surface
        surfaceW = container.width
        surfaceH = container.height
        if (visible.isEmpty) {
            visible = Rect(0, 0, surfaceW, surfaceH)
        }
        onLayoutChanged?.invoke()
        requestDraw(force = true)
    }

    fun onVisibleAreaChanged(rect: Rect) {
        val prev = visible
        visible = Rect(rect)
        if (prev.width() != visible.width() || prev.height() != visible.height()) {
            onLayoutChanged?.invoke()
        }
        requestDraw(force = true)
    }

    fun onSurfaceDestroyed() {
        surface = null
        lastFrame = null
    }

    /** Last frame painted (copy) for AA screenshot. */
    fun snapshotFrame(): Bitmap? = lastFrame?.copy(Bitmap.Config.ARGB_8888, false)

    fun requestDraw(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && now - lastDrawMs < MIN_DRAW_INTERVAL_MS) return
        val s = surface ?: return
        if (!s.isValid) return
        val strip = hudBitmap.get() ?: return
        val bounds = visible
        if (bounds.width() <= 0 || bounds.height() <= 0) return
        val theme = DriveHudTheme.forDarkMode(darkMode.get())
        try {
            val canvas = s.lockCanvas(null) ?: return
            try {
                canvas.drawColor(theme.background)
                val dst = fitRect(strip.width, strip.height, bounds)
                canvas.drawBitmap(strip, null, dst, bitmapPaint)
                // Keep a soft reference for screenshots — do not alloc a full frame every tick.
                lastFrame = strip
                lastDrawMs = now
            } finally {
                s.unlockCanvasAndPost(canvas)
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Surface draw failed", t)
        }
    }

    companion object {
        private const val TAG = "DriveHudSurface"
        private const val MIN_DRAW_INTERVAL_MS = 33L // ~30 Hz

        /** Scale [srcW]×[srcH] into [bounds] preserving aspect, centered. */
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
    }
}
