package dev.foss.expeditiongauge.car.surface

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.Surface
import androidx.car.app.SurfaceContainer
import dev.foss.expeditiongauge.car.AaDisplaySpec
import dev.foss.expeditiongauge.car.HudStripOrientation
import dev.foss.expeditiongauge.car.gauge.DriveHudTheme
import java.util.concurrent.atomic.AtomicReference

/**
 * Paints a native Drive HUD bitmap into the AA host [Surface],
 * scale-to-fit centered in the visible rect (3×1 row or 1×2 column).
 */
class DriveHudSurfacePainter {
    private val hudBitmap = AtomicReference<Bitmap?>(null)
    private val darkMode = AtomicReference(true)
    private val highContrast = AtomicReference(false)
    private val bitmapPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    @Volatile private var surface: Surface? = null
    @Volatile private var surfaceW: Int = 0
    @Volatile private var surfaceH: Int = 0
    @Volatile private var visible = Rect()
    @Volatile private var stableOrientation: HudStripOrientation = HudStripOrientation.ROW
    @Volatile private var lastDrawMs: Long = 0L
    @Volatile private var lastFrame: Bitmap? = null
    var onLayoutChanged: (() -> Unit)? = null

    fun setHudBitmap(bitmap: Bitmap?, darkBackground: Boolean, highContrastMode: Boolean = false) {
        hudBitmap.set(bitmap)
        darkMode.set(darkBackground)
        highContrast.set(highContrastMode)
    }

    fun stripOrientation(): HudStripOrientation = stableOrientation

    fun targetCubePx(): Int =
        AaDisplaySpec.surfaceCubePx(visible.width(), visible.height(), stableOrientation)

    fun isAttitudeCubeTap(x: Float, y: Float): Boolean =
        DriveHudSurfaceGeometry.isAttitudeCubeTap(
            x, y, visible, stableOrientation, hudBitmap.get(),
        )

    fun onSurfaceAvailable(container: SurfaceContainer) {
        surface = container.surface
        surfaceW = container.width
        surfaceH = container.height
        if (visible.isEmpty) {
            visible = Rect(0, 0, surfaceW, surfaceH)
            stableOrientation = HudStripOrientation.stable(
                visible.width(), visible.height(), HudStripOrientation.ROW,
            )
        }
        onLayoutChanged?.invoke()
        requestDraw(force = true)
    }

    fun onVisibleAreaChanged(rect: Rect) {
        val prev = visible
        val prevOrientation = stableOrientation
        visible = Rect(rect)
        stableOrientation = HudStripOrientation.stable(
            visible.width(), visible.height(), prevOrientation,
        )
        if (prev.width() != visible.width() || prev.height() != visible.height() ||
            prevOrientation != stableOrientation
        ) {
            onLayoutChanged?.invoke()
        }
        requestDraw(force = true)
    }

    fun onSurfaceDestroyed() {
        surface = null
        lastFrame = null
    }

    fun snapshotFrame(): Bitmap? = lastFrame?.copy(Bitmap.Config.ARGB_8888, false)

    fun requestDraw(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && now - lastDrawMs < MIN_DRAW_INTERVAL_MS) return
        val s = surface ?: return
        if (!s.isValid) return
        val strip = hudBitmap.get() ?: return
        val bounds = visible
        if (bounds.width() <= 0 || bounds.height() <= 0) return
        val theme = DriveHudTheme.forDarkMode(darkMode.get(), highContrast = highContrast.get())
        try {
            val canvas = s.lockCanvas(null) ?: return
            try {
                canvas.drawColor(theme.background)
                val dst = DriveHudSurfaceGeometry.fitRect(strip.width, strip.height, bounds)
                canvas.drawBitmap(strip, null, dst, bitmapPaint)
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
        private const val MIN_DRAW_INTERVAL_MS = 33L

        fun fitRect(srcW: Int, srcH: Int, bounds: Rect): Rect =
            DriveHudSurfaceGeometry.fitRect(srcW, srcH, bounds)
    }
}
