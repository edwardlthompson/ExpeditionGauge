package dev.foss.expeditiongauge.car.surface

import android.graphics.Rect
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DriveHudSurfacePainterHitTest {
    @Test
    fun attitudeCubeTap_leftThirdOnly() {
        val painter = DriveHudSurfacePainter()
        painter.onVisibleAreaChanged(Rect(0, 0, 900, 300))
        assertTrue(painter.isAttitudeCubeTap(100f, 150f))
        assertTrue(painter.isAttitudeCubeTap(299f, 10f))
        assertFalse(painter.isAttitudeCubeTap(300f, 150f))
        assertFalse(painter.isAttitudeCubeTap(800f, 150f))
        assertFalse(painter.isAttitudeCubeTap(100f, 400f))
    }
}
