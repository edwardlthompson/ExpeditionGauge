package dev.foss.expeditiongauge.predictiveback

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PredictiveBackTest {
    @Test
    fun coversRemainingRoutes() {
        assertTrue(PredictiveBack.covers("playback"))
        assertTrue(PredictiveBack.covers("feedback"))
        assertTrue(PredictiveBack.covers("session_comparison"))
        assertFalse(PredictiveBack.covers("dashboard"))
    }
}
