package dev.foss.expeditiongauge.tractioncircle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

class TractionCircleTest {
    @Test
    fun clampsOutsideTheCircleAndLeavesInsideUnchanged() {
        val inside = TractionCircle.clamp(0.4f, -0.3f)
        assertEquals(0.4f, inside.first, 0.001f)
        assertEquals(-0.3f, inside.second, 0.001f)
        val outside = TractionCircle.clamp(3f, 3f)
        val mag = hypot(outside.first.toDouble(), outside.second.toDouble()).toFloat()
        assertEquals(TractionCircle.MAX_G, mag, 0.01f)
        assertTrue(TractionCircle.liveTrail(gForceMode = true, recording = false))
        assertFalse(TractionCircle.liveTrail(gForceMode = false, recording = false))
    }
}
