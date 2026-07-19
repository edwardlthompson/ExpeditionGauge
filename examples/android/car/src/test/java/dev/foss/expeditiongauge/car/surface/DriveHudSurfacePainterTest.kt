package dev.foss.expeditiongauge.car.surface

import android.graphics.Rect
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DriveHudSurfacePainterTest {
    @Test
    fun fitRect_centersWideStripInLandscapeBounds() {
        val bounds = Rect(0, 0, 800, 400)
        val dst = DriveHudSurfacePainter.fitRect(srcW = 600, srcH = 200, bounds = bounds)
        assertEquals(800, dst.width())
        assertEquals(266, dst.height()) // floor(200 * 800/600)
        assertEquals(0, dst.left)
        assertEquals((400 - 266) / 2, dst.top)
    }

    @Test
    fun fitRect_centersWhenHeightBound() {
        val bounds = Rect(10, 20, 310, 220) // 300×200
        val dst = DriveHudSurfacePainter.fitRect(srcW = 300, srcH = 100, bounds = bounds)
        assertEquals(300, dst.width())
        assertEquals(100, dst.height())
        assertEquals(10, dst.left)
        assertEquals(70, dst.top)
    }
}
