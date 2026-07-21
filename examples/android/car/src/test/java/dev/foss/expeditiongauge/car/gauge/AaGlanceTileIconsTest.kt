package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AaGlanceTileIconsTest {
    @Test
    fun telemetryBitmap_hasExpectedSize() {
        val bmp = AaGlanceTileIcons.renderTelemetryBitmap(
            speedLabel = "62 MPH",
            headingLabel = "HDG 090° E",
            altLabel = "Alt 1200 ft",
            sizePx = 128,
        )
        assertEquals(128, bmp.width)
        assertEquals(128, bmp.height)
        assertTrue(bmp.config == android.graphics.Bitmap.Config.ARGB_8888)
    }

    @Test
    fun tpmsBitmap_hasExpectedSize() {
        val bmp = AaGlanceTileIcons.renderTpmsBitmap(
            fl = "32\n25C",
            fr = "32",
            rl = "--",
            rr = "--",
            sizePx = 128,
        )
        assertEquals(128, bmp.width)
        assertEquals(128, bmp.height)
    }

    @Test
    fun carIcons_buildWithoutThrow() {
        assertNotNull(
            AaGlanceTileIcons.telemetry("10 MPH", "HDG 000° N", "Alt —", sizePx = 96),
        )
        assertNotNull(
            AaGlanceTileIcons.tpms("--", "--", "--", "--", sizePx = 96),
        )
    }
}
