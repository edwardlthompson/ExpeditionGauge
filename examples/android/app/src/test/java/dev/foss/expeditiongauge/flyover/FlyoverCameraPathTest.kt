package dev.foss.expeditiongauge.flyover

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlyoverCameraPathTest {
    private val samples = listOf(
        SampleEntity(id = 1, sessionId = 1, timestampMs = 0, latitude = 0.0, longitude = 0.0, speedMps = 10f),
        SampleEntity(id = 2, sessionId = 1, timestampMs = 1000, latitude = 0.001, longitude = 0.001, speedMps = 15f),
        SampleEntity(id = 3, sessionId = 1, timestampMs = 2000, latitude = 0.002, longitude = 0.002, speedMps = 20f),
    )

    @Test
    fun buildProducesKeyframesAlongRoute() {
        val path = FlyoverCameraPath.build(samples, maxKeyframes = 3)
        assertEquals(3, path.size)
        assertEquals(0, path.first().sampleIndex)
        assertEquals(2, path.last().sampleIndex)
    }

    @Test
    fun bearingForSampleIsFinite() {
        val bearing = FlyoverCameraPath.bearingForSample(samples, 1)
        assertTrue(bearing in 0.0..360.0)
    }

    @Test
    fun hudLinesIncludeSpeedAndElevation() {
        val lines = FlyoverOverlay.hudLines(samples[1].copy(altitudeM = 120.0))
        assertTrue(lines.any { it.contains("Speed") })
        assertTrue(lines.any { it.contains("120") })
    }

    @Test
    fun nearestSampleIndexFindsClosestTimestamp() {
        val exporter = FlyoverVideoExporter()
        assertEquals(1, exporter.nearestSampleIndex(samples, 1100L))
    }
}
