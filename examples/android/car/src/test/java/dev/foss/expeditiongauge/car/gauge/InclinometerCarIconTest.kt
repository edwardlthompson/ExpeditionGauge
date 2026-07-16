package dev.foss.expeditiongauge.car.gauge

import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class InclinometerCarIconTest {
    @Test
    fun renderBitmap_returnsImmutableCopy_notSharedWithLaterRender() {
        val first = InclinometerCarIcon.renderBitmap(
            pitchDeg = 10f,
            rollDeg = 5f,
            sizePx = 128,
        )
        first.eraseColor(0xFF00FF00.toInt())

        val second = InclinometerCarIcon.renderBitmap(
            pitchDeg = -20f,
            rollDeg = -15f,
            sizePx = 128,
        )

        assertNotSame(first, second)
        // Mutating the returned copy must not affect a later render from the pool.
        assertEquals(0xFF00FF00.toInt(), first.getPixel(0, 0))
        assertEquals(false, second.getPixel(0, 0) == 0xFF00FF00.toInt())
    }

    @Test
    fun differentSizes_doNotCorruptEachOther() {
        val a = InclinometerCarIcon.renderBitmap(pitchDeg = 0f, rollDeg = 0f, sizePx = 96)
        a.eraseColor(Color.RED)
        val b = InclinometerCarIcon.renderBitmap(pitchDeg = 5f, rollDeg = 5f, sizePx = 160)
        assertEquals(Color.RED, a.getPixel(0, 0))
        assertEquals(false, b.getPixel(0, 0) == Color.RED && b.width == a.width)
    }
}
