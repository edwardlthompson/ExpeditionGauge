package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GMeterCompassBitmapTest {
    @Test
    fun gMeter_squareArgb() {
        val bmp = GMeterCarIcon.renderBitmap(3f, -2f, false, false, sizePx = 160)
        assertEquals(160, bmp.width)
        assertEquals(160, bmp.height)
    }

    @Test
    fun compass_squareArgb() {
        val bmp = CompassBallCarIcon.renderBitmap(0f, 0f, 90f, true, sizePx = 160)
        assertEquals(160, bmp.width)
        assertEquals(160, bmp.height)
    }
}
