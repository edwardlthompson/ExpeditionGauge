package dev.foss.expeditiongauge.relivechapters

import dev.foss.expeditiongauge.data.db.entities.SessionEventEntity
import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.playback.ScrubberMarkerType
import org.junit.Assert.assertEquals
import org.junit.Test

class ReliveChaptersTest {
    @Test
    fun numbersUntaggedMarksAndReadsTag() {
        val chapters = ReliveChapters.fromMarkers(
            listOf(ScrubberMarker(4, 4000, ScrubberMarkerType.MARK_EVENT, label = "Mark")),
        )
        assertEquals("Chapter 1", chapters.single().title)
        assertEquals("hairpin", ReliveChapters.tagFromPayload("""{"tag":"hairpin"}"""))
        val fromEvents = ReliveChapters.fromEvents(
            listOf(SessionEventEntity(sessionId = 1, timestampMs = 9, eventType = "mark", payloadJson = """{"tag":"apex"}""")),
        )
        assertEquals("apex", fromEvents.single().title)
    }
}
