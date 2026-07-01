package dev.foss.expeditiongauge.media

import dev.foss.expeditiongauge.playback.MediaAttachmentMarker
import dev.foss.expeditiongauge.playback.ScrubberMarkerFactory
import dev.foss.expeditiongauge.playback.ScrubberMarkerType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionMediaMarkerTest {
    @Test
    fun mediaAttachmentsBecomeScrubberMarkers() {
        val samples = listOf(
            sample(1000L),
            sample(2000L),
            sample(3000L),
        )
        val markers = ScrubberMarkerFactory.computeMarkers(
            samples = samples,
            mediaAttachments = listOf(
                MediaAttachmentMarker(mediaId = 42L, timestampMs = 2000L),
            ),
        )
        assertTrue(markers.any { it.type == ScrubberMarkerType.MEDIA_ATTACHMENT && it.mediaId == 42L })
        assertEquals(1, markers.count { it.type == ScrubberMarkerType.MEDIA_ATTACHMENT })
    }

    private fun sample(timestampMs: Long) = dev.foss.expeditiongauge.data.db.entities.SampleEntity(
        sessionId = 1L,
        timestampMs = timestampMs,
        pitchDeg = 0f,
        rollDeg = 0f,
        headingDeg = 0f,
        speedMps = 0f,
        latG = 0f,
        lonAccel = 0f,
    )
}
