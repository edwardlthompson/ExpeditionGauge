package dev.foss.expeditiongauge.offroadhold

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OffroadHoldBarsTest {
    @Test
    fun holdsTheLargerAbsoluteAngleWhenOffroad() {
        assertTrue(OffroadHoldBars.active(crawling = true, offroadPreset = false))
        assertTrue(OffroadHoldBars.active(crawling = false, offroadPreset = true))
        assertFalse(OffroadHoldBars.active(crawling = false, offroadPreset = false))
        assertEquals(22f, OffroadHoldBars.held(8f, 22f), 0.001f)
        assertEquals(-18f, OffroadHoldBars.held(-6f, -18f), 0.001f)
        assertEquals(9f, OffroadHoldBars.held(9f, 3f), 0.001f)
    }
}
