package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GaugeDisplayRotationPortraitTest {
    @Test
    fun mapAttitude_portraitApplies90Clockwise() {
        val landscape = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 0, isPortraitLayout = false)
        val portrait = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 0, isPortraitLayout = true)
        assertEquals(landscape.normalizedX, portrait.normalizedY, 0.001f)
        assertEquals(-landscape.normalizedY, portrait.normalizedX, 0.001f)
    }

    @Test
    fun negativePitch_portraitMapsToRightEdgeAfter90Cw() {
        val ball = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 0, isPortraitLayout = true)
        assertTrue(ball.normalizedX > 0.1f)
    }

    @Test
    fun rotate90Clockwise_formula() {
        val rotated = GaugeDisplayRotation.rotate90Clockwise(BallPosition(0.5f, -0.3f, GaugeZone.Safe))
        assertEquals(0.3f, rotated.normalizedX, 0.001f)
        assertEquals(0.5f, rotated.normalizedY, 0.001f)
    }
}
