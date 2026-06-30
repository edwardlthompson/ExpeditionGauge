package dev.foss.expeditiongauge.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DriftRouteStylingTest {
    @Test
    fun neutralBetaUsesYellowBucket() {
        assertEquals(DriftRouteStyling.NEUTRAL_BUCKET, DriftRouteStyling.colorBucket(2f, 0f))
    }

    @Test
    fun leftDriftUsesCyanBucket() {
        assertEquals(DriftRouteStyling.LEFT_BUCKET, DriftRouteStyling.colorBucket(12f, 0f))
    }

    @Test
    fun hardBrakeOverridesBeta() {
        assertEquals(DriftRouteStyling.BRAKE_BUCKET, DriftRouteStyling.colorBucket(12f, -0.5f))
    }

    @Test
    fun slipOverlayIncreasesWithSlip() {
        assertEquals(0f, DriftRouteStyling.slipOverlayAlpha(0.05f), 0.001f)
        assertTrue(DriftRouteStyling.slipOverlayAlpha(0.25f) > 0.4f)
    }
}
