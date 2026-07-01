package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertTrue
import org.junit.Test

/** Full display-rotation matrix — see docs/design/GMETER_HUD_ROTATION.md */
class GaugeDisplayRotationAllOrientationsTest {
    @Test
    fun portraitRotation180_brakingMirrorsWithPhone() {
        val upright = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 0, isPortraitLayout = true)
        val inverted = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 2, isPortraitLayout = true)
        assertTrue(upright.normalizedX < -0.1f)
        assertTrue(inverted.normalizedX > 0.1f)
    }

    @Test
    fun portraitRotation180_positiveRollMirrorsWithPhone() {
        val upright = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 0, isPortraitLayout = true)
        val inverted = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 2, isPortraitLayout = true)
        assertTrue(upright.normalizedY > 0.1f)
        assertTrue(inverted.normalizedY < -0.1f)
    }

    @Test
    fun landscapeRotation0_brakingTopRollRight() {
        val braking = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 0, isPortraitLayout = false)
        val roll = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 0, isPortraitLayout = false)
        assertTrue(braking.normalizedY < -0.1f)
        assertTrue(roll.normalizedX > 0.1f)
    }

    @Test
    fun landscapeRotation180_brakingTopRollRight() {
        val braking = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 2, isPortraitLayout = false)
        val roll = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 2, isPortraitLayout = false)
        assertTrue(braking.normalizedY < -0.1f)
        assertTrue(roll.normalizedX > 0.1f)
    }

    @Test
    fun portraitRotation0_matchesLockedDeviceCubeThenRotate() {
        val device = AttitudeBallLogic.mapPitchRoll(-12f, 15f)
        val expected = GaugeDisplayRotation.rotateBall(
            GaugeDisplayRotation.applyPortraitCubeRemap(device),
            displayRotation = 0,
        )
        val actual = GaugeDisplayRotation.mapAttitude(-12f, 15f, displayRotation = 0, isPortraitLayout = true)
        org.junit.Assert.assertEquals(expected.normalizedX, actual.normalizedX, 0.001f)
        org.junit.Assert.assertEquals(expected.normalizedY, actual.normalizedY, 0.001f)
    }
}
