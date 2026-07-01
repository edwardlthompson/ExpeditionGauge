package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertTrue
import org.junit.Test

class GaugeDisplayRotationLandscapeTest {
    @Test
    fun landscapeRotation90_rollMapsToScreenRight() {
        val ball = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 1, isPortraitLayout = false)
        assertTrue(ball.normalizedX > 0.1f)
    }

    @Test
    fun landscapeRotation270_rollMapsToScreenRight() {
        val ball = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 3, isPortraitLayout = false)
        assertTrue(ball.normalizedX > 0.1f)
    }

    @Test
    fun landscapeRotation90_brakingMapsToScreenTop() {
        val ball = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 1, isPortraitLayout = false)
        assertTrue(ball.normalizedY < -0.1f)
    }

    @Test
    fun landscapeRotation270_brakingMapsToScreenTop() {
        val ball = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 3, isPortraitLayout = false)
        assertTrue(ball.normalizedY < -0.1f)
    }

    @Test
    fun rotate90CounterClockwise_invertsClockwise() {
        val base = BallPosition(0.5f, -0.3f, GaugeZone.Safe)
        val restored = GaugeDisplayRotation.rotate90CounterClockwise(
            GaugeDisplayRotation.rotate90Clockwise(base),
        )
        assertTrue(kotlin.math.abs(restored.normalizedX - base.normalizedX) < 0.001f)
        assertTrue(kotlin.math.abs(restored.normalizedY - base.normalizedY) < 0.001f)
    }
}
