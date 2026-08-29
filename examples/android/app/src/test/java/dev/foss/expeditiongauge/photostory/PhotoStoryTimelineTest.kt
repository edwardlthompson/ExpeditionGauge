package dev.foss.expeditiongauge.photostory

import dev.foss.expeditiongauge.data.db.entities.SessionMediaEntity
import dev.foss.expeditiongauge.data.db.entities.SessionMediaKind
import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.playback.ScrubberMarkerType
import org.junit.Assert.assertEquals
import org.junit.Test

class PhotoStoryTimelineTest {
    @Test
    fun ordersPhotosAndIgnoresVideo() {
        val items = PhotoStoryTimeline.fromMedia(
            listOf(
                SessionMediaEntity(id = 2, sessionId = 1, timestampMs = 2000, fileName = "b.jpg", mimeType = "image/jpeg"),
                SessionMediaEntity(id = 1, sessionId = 1, timestampMs = 1000, fileName = "a.jpg", mimeType = "image/jpeg"),
                SessionMediaEntity(
                    id = 3,
                    sessionId = 1,
                    timestampMs = 1500,
                    fileName = "c.mp4",
                    mimeType = "video/mp4",
                    mediaKind = SessionMediaKind.VIDEO,
                ),
            ),
        )
        assertEquals(listOf("a.jpg", "b.jpg"), items.map { it.label })
        val fromMarks = PhotoStoryTimeline.fromMarkers(
            listOf(ScrubberMarker(5, 9, ScrubberMarkerType.MEDIA_ATTACHMENT, label = "apex", mediaId = 8)),
        )
        assertEquals(8L, fromMarks.single().mediaId)
    }
}
