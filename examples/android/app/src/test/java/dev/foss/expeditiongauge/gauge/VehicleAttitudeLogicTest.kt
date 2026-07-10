package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Test

class VehicleAttitudeLogicTest {
    @Test
    fun portraitRotation0_matchesLockedInclinometerSwap() {
        val (pitch, roll) = VehicleAttitudeLogic.fromDevice(-12f, 15f, displayRotation = 0)
        assertEquals(15f, pitch, 0.001f)
        assertEquals(-12f, roll, 0.001f)
    }

    @Test
    fun screenStableFusion_alwaysUsesPortraitSwap() {
        // After SensorAxisRemap, fusion always passes displayRotation=0.
        val (pitch, roll) = VehicleAttitudeLogic.fromDevice(
            devicePitchDeg = -5f,
            deviceRollDeg = 2f,
            displayRotation = 0,
        )
        assertEquals(2f, pitch, 0.001f)
        assertEquals(-5f, roll, 0.001f)
    }
}
