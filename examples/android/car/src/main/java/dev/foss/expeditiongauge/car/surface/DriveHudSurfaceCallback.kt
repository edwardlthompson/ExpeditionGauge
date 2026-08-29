package dev.foss.expeditiongauge.car.surface

import android.graphics.Rect
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer

/** Forwards host Surface lifecycle to [DriveHudSurfacePainter]; optional tap handler. */
class DriveHudSurfaceCallback(
    private val painter: DriveHudSurfacePainter,
    private val onAttitudeTap: (() -> Unit)? = null,
    private val onDtcFooterTap: (() -> Unit)? = null,
) : SurfaceCallback {
    override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
        painter.onSurfaceAvailable(surfaceContainer)
    }

    override fun onVisibleAreaChanged(visibleArea: Rect) {
        painter.onVisibleAreaChanged(visibleArea)
    }

    override fun onStableAreaChanged(stableArea: Rect) {
        // Prefer visible area for layout; stable reserved for future chrome insets.
    }

    override fun onSurfaceDestroyed(surfaceContainer: SurfaceContainer) {
        painter.onSurfaceDestroyed()
    }

    override fun onScroll(distanceX: Float, distanceY: Float) = Unit

    override fun onFling(velocityX: Float, velocityY: Float) = Unit

    override fun onScale(focusX: Float, focusY: Float, scaleFactor: Float) = Unit

    override fun onClick(x: Float, y: Float) {
        if (painter.isDtcFooterTap(x, y)) {
            onDtcFooterTap?.invoke()
        } else if (painter.isAttitudeCubeTap(x, y)) {
            onAttitudeTap?.invoke()
        }
    }
}
