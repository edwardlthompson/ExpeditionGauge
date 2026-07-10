package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Landscape G-meter: vehicle-frame attitude — displayRotation ignored (cancelled in fusion). */
class GaugeDisplayRotationLandscapeTest {
    @Test
    fun landscape_rollMapsToScreenRight() {
        val ball = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 1, isPortraitLayout = false)
        assertTrue(ball.normalizedX > 0.1f)
    }

    @Test
    fun landscape_brakingMapsToScreenTop() {
        val ball = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 1, isPortraitLayout = false)
        assertTrue(ball.normalizedY < -0.1f)
    }

    @Test
    fun landscape_stableAcross90And270() {
        val a = GaugeDisplayRotation.mapAttitude(-12f, 15f, displayRotation = 1, isPortraitLayout = false)
        val b = GaugeDisplayRotation.mapAttitude(-12f, 15f, displayRotation = 3, isPortraitLayout = false)
        assertEquals(a.normalizedX, b.normalizedX, 0.001f)
        assertEquals(a.normalizedY, b.normalizedY, 0.001f)
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
