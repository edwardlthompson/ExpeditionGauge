package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScrubberMarkerFactoryTest {
    @Test
    fun detectsHighSlipAndBeta() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1L, timestampMs = 0, driftAngleDeg = 20f, slipRatio = 0.2f),
        )
        val markers = ScrubberMarkerFactory.computeMarkers(samples, betaThreshold = 15f, slipThreshold = 0.15f)
        assertEquals(2, markers.size)
        assertTrue(markers.any { it.type == ScrubberMarkerType.HIGH_BETA })
        assertTrue(markers.any { it.type == ScrubberMarkerType.HIGH_SLIP })
    }

    @Test
    fun addsAlertMarkers() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1L, timestampMs = 0),
            SampleEntity(id = 2, sessionId = 1L, timestampMs = 1000),
            SampleEntity(id = 3, sessionId = 1L, timestampMs = 2000),
        )
        val markers = ScrubberMarkerFactory.computeMarkers(samples, alertTimestamps = listOf(1500L))
        assertTrue(markers.any { it.type == ScrubberMarkerType.ALERT })
    }
}
