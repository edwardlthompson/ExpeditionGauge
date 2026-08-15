package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PedalBarLogicTest {
    @Test
    fun restIsCenter() {
        val s = PedalBarLogic.from(throttlePct = 0f, lonG = 0f)
        assertEquals(0f, s.position, 0.001f)
        assertFalse(s.flashThrottle)
        assertFalse(s.flashBrake)
    }

    @Test
    fun fullThrottleRightAndFlash() {
        val s = PedalBarLogic.from(throttlePct = 100f, lonG = -0.4f)
        assertEquals(1f, s.position, 0.001f)
        assertTrue(s.flashThrottle)
        assertEquals(0f, s.brake01, 0.001f)
    }

    @Test
    fun brakeFromLonGWhenThrottleClosed() {
        val s = PedalBarLogic.from(throttlePct = 0f, lonG = -0.85f)
        assertEquals(-1f, s.position, 0.001f)
        assertTrue(s.flashBrake)
    }
}
