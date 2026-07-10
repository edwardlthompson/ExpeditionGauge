package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class InclinometerAxisMapTest {
    @Test
    fun vehicleFrame_passthrough() {
        val (pitch, roll) = GaugeDisplayRotation.mapFusionToInclinometerAxes(
            pitchDeg = -12f,
            rollDeg = 15f,
            isPortraitLayout = true,
            displayRotation = 0,
        )
        assertEquals(-12f, pitch, 0.001f)
        assertEquals(15f, roll, 0.001f)
    }

    @Test
    fun landscapeAlsoPassthrough_vehicleFrame() {
        val (pitch, roll) = GaugeDisplayRotation.mapFusionToInclinometerAxes(
            pitchDeg = 3f,
            rollDeg = -2f,
            isPortraitLayout = false,
            displayRotation = 1,
        )
        assertEquals(3f, pitch, 0.001f)
        assertEquals(-2f, roll, 0.001f)
    }
}
