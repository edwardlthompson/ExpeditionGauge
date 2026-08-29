package dev.foss.expeditiongauge.shiftlight

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShiftLightTest {
    @Test
    fun lightsAtOrAboveThreshold() {
        assertFalse(ShiftLight.active(null))
        assertFalse(ShiftLight.active(5_499f))
        assertTrue(ShiftLight.active(5_500f))
        assertTrue(ShiftLight.active(6_200f, thresholdRpm = 6_000f))
        assertEquals(4_000f, ShiftLight.threshold(4_000f), 0.01f)
        assertEquals(ShiftLight.DEFAULT_RPM, ShiftLight.threshold(null), 0.01f)
    }
}
