package dev.foss.expeditiongauge.media

import android.app.Activity
import android.graphics.Rect
import android.view.Window
import dev.foss.expeditiongauge.ui.dashboard.hud.HudCubeScreenshotBounds
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Captures HUD cubes via window [android.view.PixelCopy] using published screen bounds.
 * Combined mode crops the stack union; per-cube mode saves one square JPEG each.
 */
object HudScreenshotSaver {
    private val capturing = AtomicBoolean(false)

    sealed class Result {
        data object Success : Result()
        data object Busy : Result()
        data object Failed : Result()
        data class Partial(val saved: Int, val total: Int) : Result()
    }

    fun captureToGallery(activity: Activity, onComplete: (Result) -> Unit) {
        if (!capturing.compareAndSet(false, true)) {
            onComplete(Result.Busy)
            return
        }
        val window = activity.window
        val decor = window.decorView
        val winW = decor.width
        val winH = decor.height
        if (winW <= 0 || winH <= 0) {
            capturing.set(false)
            onComplete(Result.Failed)
            return
        }
        val src = Rect(0, 0, winW, winH)
        HudScreenshotIo.copyRect(window, src) { bitmap ->
            try {
                if (bitmap == null) {
                    onComplete(Result.Failed)
                    return@copyRect
                }
                val saved = HudScreenshotIo.insertBitmap(activity, bitmap, suffix = null)
                bitmap.recycle()
                onComplete(if (saved) Result.Success else Result.Failed)
            } finally {
                capturing.set(false)
            }
        }
    }

    /** Capture each cube tile as its own square gallery image. */
    fun captureEachCubeToGallery(activity: Activity, onComplete: (Result) -> Unit) {
        if (!capturing.compareAndSet(false, true)) {
            onComplete(Result.Busy)
            return
        }
        val window = activity.window
        val decor = window.decorView
        val winW = decor.width
        val winH = decor.height
        val tiles = HudCubeScreenshotBounds.snapshotTiles()
            .mapNotNull { HudScreenshotIo.clampRect(it, winW, winH)?.let(HudScreenshotIo::toSquare) }
        if (tiles.isEmpty() || winW <= 0 || winH <= 0) {
            capturing.set(false)
            onComplete(Result.Failed)
            return
        }
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        captureTilesSequentially(window, tiles, 0, 0, stamp, activity) { saved, total ->
            capturing.set(false)
            onComplete(
                when {
                    saved == 0 -> Result.Failed
                    saved == total -> Result.Success
                    else -> Result.Partial(saved, total)
                },
            )
        }
    }

    private fun captureTilesSequentially(
        window: Window,
        tiles: List<Rect>,
        index: Int,
        saved: Int,
        stamp: String,
        activity: Activity,
        onDone: (saved: Int, total: Int) -> Unit,
    ) {
        if (index >= tiles.size) {
            onDone(saved, tiles.size)
            return
        }
        HudScreenshotIo.copyRect(window, tiles[index]) { bitmap ->
            var nextSaved = saved
            if (bitmap != null) {
                if (HudScreenshotIo.insertBitmap(activity, bitmap, "_${index + 1}", stamp)) {
                    nextSaved++
                }
                bitmap.recycle()
            }
            captureTilesSequentially(window, tiles, index + 1, nextSaved, stamp, activity, onDone)
        }
    }
}
