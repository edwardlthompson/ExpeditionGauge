package dev.foss.expeditiongauge.fusion

import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

class MadgwickFilterTest {
    private lateinit var filter: MadgwickFilter

    @Before
    fun setUp() {
        filter = MadgwickFilter()
    }

    @Test
    fun levelAccelProducesNearZeroPitchRoll() {
        repeat(50) {
            filter.update(0f, 0f, 0f, 0f, 0f, 9.81f)
        }
        assertTrue(abs(filter.pitchDeg()) < 5f)
        assertTrue(abs(filter.rollDeg()) < 5f)
    }

    @Test
    fun gyroIntegrationChangesYaw() {
        repeat(50) {
            filter.update(0f, 0f, 0.1f, 0f, 0f, 9.81f)
        }
        assertTrue(abs(filter.yawDeg()) > 0.1f)
    }
}
