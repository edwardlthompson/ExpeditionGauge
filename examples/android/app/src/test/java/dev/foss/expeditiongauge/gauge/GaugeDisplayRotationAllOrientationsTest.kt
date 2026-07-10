package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Vehicle-frame attitude: phone displayRotation is cancelled in fusion.
 * G-meter mapAttitude applies portrait cube only — same vehicle pose → same ball.
 */
class GaugeDisplayRotationAllOrientationsTest {
    @Test
    fun portrait_sameVehicleAttitude_stableAcrossDisplayRotation() {
        val upright = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 0, isPortraitLayout = true)
        val inverted = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 2, isPortraitLayout = true)
        assertEquals(upright.normalizedX, inverted.normalizedX, 0.001f)
        assertEquals(upright.normalizedY, inverted.normalizedY, 0.001f)
        assertTrue(upright.normalizedX < -0.1f)
    }

    @Test
    fun portrait_positiveRoll_stableAcrossDisplayRotation() {
        val upright = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 0, isPortraitLayout = true)
        val inverted = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 2, isPortraitLayout = true)
        assertEquals(upright.normalizedX, inverted.normalizedX, 0.001f)
        assertEquals(upright.normalizedY, inverted.normalizedY, 0.001f)
        assertTrue(upright.normalizedY > 0.1f)
    }

    @Test
    fun landscape_brakingTopRollRight() {
        val braking = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 0, isPortraitLayout = false)
        val roll = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 0, isPortraitLayout = false)
        assertTrue(braking.normalizedY < -0.1f)
        assertTrue(roll.normalizedX > 0.1f)
    }

    @Test
    fun landscape_sameVehicleAttitude_stableAcrossDisplayRotation() {
        val a = GaugeDisplayRotation.mapAttitude(-12f, 15f, displayRotation = 1, isPortraitLayout = false)
        val b = GaugeDisplayRotation.mapAttitude(-12f, 15f, displayRotation = 3, isPortraitLayout = false)
        assertEquals(a.normalizedX, b.normalizedX, 0.001f)
        assertEquals(a.normalizedY, b.normalizedY, 0.001f)
    }

    @Test
    fun portraitRotation0_appliesLockedCubeRemap() {
        val device = AttitudeBallLogic.mapPitchRoll(-12f, 15f)
        val expected = GaugeDisplayRotation.applyPortraitCubeRemap(device)
        val actual = GaugeDisplayRotation.mapAttitude(-12f, 15f, displayRotation = 0, isPortraitLayout = true)
        assertEquals(expected.normalizedX, actual.normalizedX, 0.001f)
        assertEquals(expected.normalizedY, actual.normalizedY, 0.001f)
    }
}
