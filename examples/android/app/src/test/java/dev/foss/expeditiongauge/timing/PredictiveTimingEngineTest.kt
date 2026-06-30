package dev.foss.expeditiongauge.timing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PredictiveTimingEngineTest {
    @Test
    fun formatDeltaShowsSign() {
        val engine = PredictiveTimingEngine()
        assertEquals("+0.5s", engine.formatDelta(500))
        assertTrue(engine.formatDelta(-300).startsWith("-"))
    }

    @Test
    fun formatLapTimeUnderOneMinute() {
        val engine = PredictiveTimingEngine()
        assertEquals("45.50", engine.formatLapTime(45_500))
    }
}
