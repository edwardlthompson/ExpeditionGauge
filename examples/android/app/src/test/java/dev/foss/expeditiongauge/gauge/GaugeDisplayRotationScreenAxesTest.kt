package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class GaugeDisplayRotationScreenAxesTest {
    @Test
    fun portraitRotation0_pitchOnLadder_rollOnSides() {
        // Portrait G-meter: pitch → X, roll → Y; inclinometer remaps X→ladder, Y→sides.
        val (pitch, roll) = GaugeDisplayRotation.mapAttitudeToScreenAxes(
            pitchDeg = -12f,
            rollDeg = 15f,
            displayRotation = 0,
            isPortraitLayout = true,
        )
        assertTrue("physical pitch should drive ladder", abs(pitch) > 0.1f)
        assertTrue("physical roll should drive sides", abs(roll) > 0.1f)
        // Braking (neg pitch) → left on portrait G-meter X → negative ladder after remap
        assertTrue(pitch < -0.1f)
        assertTrue(roll > 0.1f)
    }

    @Test
    fun landscapeRotation0_pitchVertical_rollHorizontal() {
        val (pitch, roll) = GaugeDisplayRotation.mapAttitudeToScreenAxes(
            pitchDeg = -12f,
            rollDeg = 15f,
            displayRotation = 0,
            isPortraitLayout = false,
        )
        assertEquals(-12f, pitch, 0.2f)
        assertEquals(15f, roll, 0.2f)
    }

    @Test
    fun landscapeRotation90_keepsPitchOnLadder() {
        val (pitch, roll) = GaugeDisplayRotation.mapAttitudeToScreenAxes(
            pitchDeg = -12f,
            rollDeg = 0f,
            displayRotation = 1,
            isPortraitLayout = false,
        )
        assertTrue(abs(pitch) > abs(roll))
        assertTrue(pitch < -0.1f)
    }

    @Test
    fun portraitPurePitch_doesNotFillSides() {
        val (pitch, roll) = GaugeDisplayRotation.mapAttitudeToScreenAxes(
            pitchDeg = 20f,
            rollDeg = 0f,
            displayRotation = 0,
            isPortraitLayout = true,
        )
        assertTrue(abs(pitch) > 0.1f)
        assertEquals(0f, roll, 0.2f)
    }

    @Test
    fun portraitPureRoll_doesNotLightLadder() {
        val (pitch, roll) = GaugeDisplayRotation.mapAttitudeToScreenAxes(
            pitchDeg = 0f,
            rollDeg = 20f,
            displayRotation = 0,
            isPortraitLayout = true,
        )
        assertEquals(0f, pitch, 0.2f)
        assertTrue(abs(roll) > 0.1f)
    }
}
