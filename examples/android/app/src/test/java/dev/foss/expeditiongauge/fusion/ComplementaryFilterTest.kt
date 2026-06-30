package dev.foss.expeditiongauge.fusion

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ComplementaryFilterTest {
    @Test
    fun levelAccelYieldsNearZeroPitchRoll() {
        val filter = ComplementaryFilter()
        repeat(20) {
            filter.update(0f, 0f, 0f, 0f, 0f, 9.81f, 0.02f)
        }
        assertTrue(abs(filter.pitchDeg()) < 5f)
        assertTrue(abs(filter.rollDeg()) < 5f)
    }
}
