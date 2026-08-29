package dev.foss.expeditiongauge.playbackbookmarks

import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.playback.ScrubberMarkerType
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackBookmarksTest {
    @Test
    fun keepsOnlyMarkEvents() {
        val marks = PlaybackBookmarks.fromMarkers(
            listOf(
                ScrubberMarker(1, 1000, ScrubberMarkerType.ALERT),
                ScrubberMarker(2, 2000, ScrubberMarkerType.MARK_EVENT, label = "Hairpin"),
                ScrubberMarker(2, 2000, ScrubberMarkerType.MARK_EVENT, label = "Dup"),
            ),
        )
        assertEquals(1, marks.size)
        assertEquals("Hairpin", marks[0].label)
        assertEquals(2, marks[0].sampleIndex)
    }
}
