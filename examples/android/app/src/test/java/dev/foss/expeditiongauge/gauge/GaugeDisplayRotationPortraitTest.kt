package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Portrait G-meter: roll lateral, pitch vertical — braking toward front/top. */
class GaugeDisplayRotationPortraitTest {
    @Test
    fun mapAttitude_portraitKeepsDeviceAxes() {
        val device = AttitudeBallLogic.mapPitchRoll(12f, 15f)
        val portrait = GaugeDisplayRotation.mapAttitude(12f, 15f, displayRotation = 0, isPortraitLayout = true)
        assertEquals(device.normalizedX, portrait.normalizedX, 0.001f)
        assertEquals(device.normalizedY, portrait.normalizedY, 0.001f)
    }

    @Test
    fun positiveRoll_portraitMapsRightOnHorizontalAxis() {
        val ball = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 0, isPortraitLayout = true)
        assertTrue(ball.normalizedX > 0.1f)
        assertTrue(kotlin.math.abs(ball.normalizedY) < 0.05f)
    }

    @Test
    fun braking_portraitMapsBallTowardFrontTop() {
        // −pitch (braking / nose down) → negative screen Y (toward top / vehicle front)
        val ball = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 0, isPortraitLayout = true)
        assertTrue(ball.normalizedY < -0.1f)
        assertTrue(kotlin.math.abs(ball.normalizedX) < 0.05f)
    }

    @Test
    fun accel_portraitMapsBallTowardRearBottom() {
        val ball = GaugeDisplayRotation.mapAttitude(12f, 0f, displayRotation = 0, isPortraitLayout = true)
        assertTrue(ball.normalizedY > 0.1f)
        assertTrue(kotlin.math.abs(ball.normalizedX) < 0.05f)
    }

    @Test
    fun mapGForce_portraitKeepsLatOnX() {
        val ball = GaugeDisplayRotation.mapGForce(0.5f, 0f, displayRotation = 0, isPortraitLayout = true)
        assertTrue(ball.normalizedX > 0.1f)
    }
}
