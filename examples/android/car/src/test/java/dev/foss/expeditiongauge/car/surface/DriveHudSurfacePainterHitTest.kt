package dev.foss.expeditiongauge.car.surface

import android.graphics.Bitmap
import android.graphics.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import dev.foss.expeditiongauge.car.HudStripOrientation

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DriveHudSurfacePainterHitTest {
    @Test
    fun attitudeCubeTap_leftThirdWhenWide() {
        val painter = DriveHudSurfacePainter()
        painter.onVisibleAreaChanged(Rect(0, 0, 900, 300))
        assertEquals(HudStripOrientation.ROW, painter.stripOrientation())
        assertTrue(painter.isAttitudeCubeTap(100f, 150f))
        assertTrue(painter.isAttitudeCubeTap(299f, 10f))
        assertFalse(painter.isAttitudeCubeTap(300f, 150f))
        assertTrue(painter.isTelemetryCubeTap(450f, 150f))
        assertFalse(painter.isAttitudeCubeTap(800f, 150f))
        assertFalse(painter.isAttitudeCubeTap(100f, 400f))
    }

    @Test
    fun attitudeCubeTap_excludesRowFooterBand() {
        val painter = DriveHudSurfacePainter()
        painter.onVisibleAreaChanged(Rect(0, 0, 840, 320))
        // 3×1 cubes + footer: 840×(280+40)
        val bmp = Bitmap.createBitmap(840, 320, Bitmap.Config.ARGB_8888)
        painter.setHudBitmap(bmp, darkBackground = true)
        assertTrue(painter.isAttitudeCubeTap(100f, 100f))
        // Bottom of fitted strip is footer (cube band = 280/320 of dest height)
        assertFalse(painter.isAttitudeCubeTap(100f, 310f))
        assertTrue(painter.isDtcFooterTap(100f, 310f))
        assertFalse(painter.isDtcFooterTap(100f, 100f))
    }

    @Test
    fun attitudeCubeTap_topHalfWhenTall() {
        val painter = DriveHudSurfacePainter()
        painter.onVisibleAreaChanged(Rect(0, 0, 480, 800))
        assertEquals(HudStripOrientation.COLUMN, painter.stripOrientation())
        assertTrue(painter.isAttitudeCubeTap(240f, 100f))
        assertTrue(painter.isAttitudeCubeTap(10f, 399f))
        assertFalse(painter.isAttitudeCubeTap(240f, 400f))
        assertFalse(painter.isAttitudeCubeTap(240f, 700f))
        assertFalse(painter.isAttitudeCubeTap(-1f, 100f))
    }

    @Test
    fun emptyVisible_rejectsTap() {
        val painter = DriveHudSurfacePainter()
        assertFalse(painter.isAttitudeCubeTap(10f, 10f))
    }
}
