package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertTrue
import org.junit.Test

class GaugeDisplayRotationTest {
    @Test
    fun forwardLonGMovesBallUpAtRotation0() {
        val ball = GaugeDisplayRotation.mapGForce(latG = 0f, lonG = 1f, displayRotation = 0)
        assertTrue(ball.normalizedY < 0f)
    }

    @Test
    fun lateralLatGMovesBallRightAtRotation0() {
        val ball = GaugeDisplayRotation.mapGForce(latG = 1f, lonG = 0f, displayRotation = 0)
        assertTrue(ball.normalizedX > 0f)
    }

    @Test
    fun forwardLonGMovesBallUpAtRotation90() {
        val ball = GaugeDisplayRotation.mapGForce(latG = 0f, lonG = 1f, displayRotation = 1)
        assertTrue(ball.normalizedX > 0f)
    }

    @Test
    fun rotatePreservesMagnitudeWithinUnitCircle() {
        val base = GForceBallLogic.mapLatLonG(0.8f, -0.6f)
        val rotated = GaugeDisplayRotation.rotateBall(base, 2)
        val mag = kotlin.math.hypot(rotated.normalizedX.toDouble(), rotated.normalizedY.toDouble())
        assertTrue(mag <= 1.01)
    }
}
