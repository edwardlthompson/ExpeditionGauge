package dev.foss.expeditiongauge.playbackbookmarks

import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.playback.ScrubberMarkerType

data class PlaybackBookmark(
    val sampleIndex: Int,
    val timestampMs: Long,
    val label: String,
)

/** Jump list built from mark-event scrubber dots. */
object PlaybackBookmarks {
    fun fromMarkers(markers: List<ScrubberMarker>): List<PlaybackBookmark> =
        markers.filter { it.type == ScrubberMarkerType.MARK_EVENT }
            .distinctBy { it.sampleIndex }
            .map { PlaybackBookmark(it.sampleIndex, it.timestampMs, it.label ?: "Mark") }
}
