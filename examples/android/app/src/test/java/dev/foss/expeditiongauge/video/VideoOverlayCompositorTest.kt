package dev.foss.expeditiongauge.video

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoOverlayCompositorTest {
    private val samples = listOf(
        SampleEntity(sessionId = 1, timestampMs = 1000L, speedMps = 10f, driftAngleDeg = 5f, latG = 0.8f),
        SampleEntity(sessionId = 1, timestampMs = 2000L, speedMps = 20f, driftAngleDeg = 15f, latG = 1.1f),
    )

    @Test
    fun nearestSamplePicksClosestTimestamp() {
        val nearest = VideoOverlayCompositor.nearestSample(samples, 1900L)
        assertEquals(2000L, nearest?.timestampMs)
    }

    @Test
    fun overlayLinesIncludeSpeedAndBeta() {
        val lines = VideoOverlayCompositor.overlayLines(samples[1])
        assertTrue(lines.any { it.contains("72") })
        assertTrue(lines.any { it.contains("β") })
    }
}
