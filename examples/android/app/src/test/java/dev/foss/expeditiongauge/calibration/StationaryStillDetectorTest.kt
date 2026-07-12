package dev.foss.expeditiongauge.calibration

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StationaryStillDetectorTest {
    @Test
    fun nullSensors_neverStill() {
        val d = StationaryStillDetector(holdMs = 100L)
        assertFalse(
            d.onSample(
                nowMs = 1_000L,
                accelX = null, accelY = 0f, accelZ = 9.81f,
                gyroX = 0f, gyroY = 0f, gyroZ = 0f,
            ),
        )
        assertFalse(
            d.onSample(
                nowMs = 2_000L,
                accelX = 0f, accelY = 0f, accelZ = 9.81f,
                gyroX = null, gyroY = 0f, gyroZ = 0f,
            ),
        )
    }

    @Test
    fun stillWindow_requiresHoldMs() {
        val d = StationaryStillDetector(holdMs = 100L)
        assertFalse(
            d.onSample(50L, 0f, 0f, 9.81f, 0f, 0f, 0f),
        )
        assertTrue(
            d.onSample(160L, 0f, 0f, 9.81f, 0f, 0f, 0f),
        )
    }

    @Test
    fun motion_resetsStillWindow() {
        val d = StationaryStillDetector(holdMs = 100L)
        d.onSample(0L, 0f, 0f, 9.81f, 0f, 0f, 0f)
        assertFalse(d.onSample(50L, 0f, 0f, 9.81f, 1f, 0f, 0f))
        assertFalse(d.onSample(160L, 0f, 0f, 9.81f, 0f, 0f, 0f))
        assertTrue(d.onSample(270L, 0f, 0f, 9.81f, 0f, 0f, 0f))
    }
}
