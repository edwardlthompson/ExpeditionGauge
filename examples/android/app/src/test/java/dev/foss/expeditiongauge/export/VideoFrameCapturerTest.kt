package dev.foss.expeditiongauge.export

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoFrameCapturerTest {
    @Test
    fun frameCountRespectsClipCap() {
        val settings = PlaybackVideoExportSettings(clipDurationMs = 30_000L, frameRate = 10)
        assertEquals(300, VideoFrameCapturer.frameCount(600_000L, settings))
    }

    @Test
    fun sampleIndexForFrameSpansSession() {
        assertEquals(0, VideoFrameCapturer.sampleIndexForFrame(10, 0, 5))
        assertEquals(9, VideoFrameCapturer.sampleIndexForFrame(10, 4, 5))
    }

    @Test
    fun normalizedRouteNeedsGps() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1, timestampMs = 0, latitude = 0.0, longitude = 0.0),
            SampleEntity(id = 2, sessionId = 1, timestampMs = 100, latitude = 1.0, longitude = 1.0),
        )
        assertEquals(2, VideoFrameCapturer.normalizedRoute(samples, 1).size)
    }

    @Test
    fun playbackExportLinesIncludePitchRoll() {
        val sample = SampleEntity(
            id = 1,
            sessionId = 1,
            timestampMs = 0,
            speedMps = 10f,
            pitchDeg = 5f,
            rollDeg = -3f,
        )
        val lines = dev.foss.expeditiongauge.video.VideoOverlayCompositor.playbackExportLines(sample)
        assertTrue(lines.any { it.contains("pitch") })
        assertTrue(lines.any { it.contains("roll") })
    }
}
