package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Locks portrait G-meter to pitch mirror + 90° CW — see docs/design/GMETER_HUD_ROTATION.md */
class GaugeDisplayRotationPortraitTest {
    @Test
    fun mapAttitude_portraitAppliesPitchMirrorThen90Clockwise() {
        val mirrored = GaugeDisplayRotation.rotate90Clockwise(
            BallPosition(0.5f, -0.4f, GaugeZone.Safe),
        )
        val portrait = GaugeDisplayRotation.mapAttitude(12f, 15f, displayRotation = 0, isPortraitLayout = true)
        assertEquals(mirrored.normalizedX, portrait.normalizedX, 0.001f)
        assertEquals(mirrored.normalizedY, portrait.normalizedY, 0.001f)
    }

    @Test
    fun positiveRoll_portraitMapsToBottomOnVerticalAxis() {
        val ball = GaugeDisplayRotation.mapAttitude(0f, 15f, displayRotation = 0, isPortraitLayout = true)
        assertTrue(ball.normalizedY > 0.1f)
    }

    @Test
    fun negativePitch_portraitMapsToLeftAfterPitchMirror() {
        val ball = GaugeDisplayRotation.mapAttitude(-12f, 0f, displayRotation = 0, isPortraitLayout = true)
        assertTrue(ball.normalizedX < -0.1f)
    }

    @Test
    fun rotate90Clockwise_mapsPitchToXAndRollToY() {
        val rotated = GaugeDisplayRotation.rotate90Clockwise(
            BallPosition(0.5f, -0.3f, GaugeZone.Safe),
        )
        assertEquals(0.3f, rotated.normalizedX, 0.001f)
        assertEquals(0.5f, rotated.normalizedY, 0.001f)
    }
}
