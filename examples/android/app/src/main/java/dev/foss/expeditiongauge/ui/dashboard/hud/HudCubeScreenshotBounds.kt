package dev.foss.expeditiongauge.ui.dashboard.hud

import android.graphics.Rect
import java.util.concurrent.atomic.AtomicReference

/**
 * Window-space bounds for HUD screenshots.
 * [union] covers the full cube stack; [tiles] are per-cube squares (screen coordinates).
 */
object HudCubeScreenshotBounds {
    private val union = AtomicReference<Rect?>(null)
    private val tiles = AtomicReference<List<Rect>>(emptyList())

    fun updateUnion(left: Int, top: Int, right: Int, bottom: Int) {
        if (right > left && bottom > top) {
            union.set(Rect(left, top, right, bottom))
        }
    }

    fun updateTiles(rects: List<Rect>) {
        tiles.set(rects.filter { it.width() > 8 && it.height() > 8 }.map { Rect(it) })
    }

    fun snapshotUnion(): Rect? = union.get()?.let { Rect(it) }

    /** Screen coordinates for each cube tile (order matches on-screen stack). */
    fun snapshotTiles(): List<Rect> = tiles.get().map { Rect(it) }

    /** @deprecated Prefer [snapshotUnion]. */
    fun snapshot(): Rect? = snapshotUnion()

    fun update(left: Int, top: Int, right: Int, bottom: Int) =
        updateUnion(left, top, right, bottom)
}
