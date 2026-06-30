package dev.foss.expeditiongauge.drift

import org.junit.Assert.assertEquals
import org.junit.Test

class SideslipEkfTest {
    @Test
    fun velocityHeadingUpdateComputesBeta() {
        val ekf = SideslipEkf(minSpeedMps = 1f)
        ekf.updateBodyYaw(90f)
        ekf.updateVelocityHeading(45f, speedMps = 5f)
        assertEquals(45f, ekf.currentBeta(), 0.001f)
    }

    @Test
    fun lowSpeedSuppressesBetaUpdate() {
        val ekf = SideslipEkf(minSpeedMps = 2f)
        ekf.updateBodyYaw(90f)
        ekf.updateVelocityHeading(0f, speedMps = 1f)
        assertEquals(0f, ekf.currentBeta(), 0.001f)
    }

    @Test
    fun normalizeAngleWrapsCorrectly() {
        assertEquals(-10f, normalizeAngleDeg(350f), 0.001f)
        assertEquals(10f, normalizeAngleDeg(370f), 0.001f)
    }
}
