package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DriveHudDtcFooterPaintTest {
    @Test
    fun fitTextSize_expandsShortLineTowardMax() {
        // width grows with size; at 48 still under 200 → pick max
        val size = DriveHudDtcFooterPaint.fitTextSize(200f, 16f, 48f) { it * 2f }
        assertEquals(48f, size, 0.01f)
    }

    @Test
    fun fitTextSize_shrinksLongLineToFit() {
        // width = 10 * size → maxWidth 200 ⇒ size ≤ 20
        val size = DriveHudDtcFooterPaint.fitTextSize(200f, 16f, 48f) { it * 10f }
        assertTrue("expected ~20, got $size", size in 19.5f..20.5f)
        assertTrue(size * 10f <= 200.5f)
    }
}
