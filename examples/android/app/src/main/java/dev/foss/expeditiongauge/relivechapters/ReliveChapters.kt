package dev.foss.expeditiongauge.relivechapters

import dev.foss.expeditiongauge.data.db.entities.SessionEventEntity
import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.playbackbookmarks.PlaybackBookmarks

data class ReliveChapter(
    val timestampMs: Long,
    val title: String,
    val sampleIndex: Int,
)

/** Numbered Relive chapters from mark events. */
object ReliveChapters {
    fun fromMarkers(markers: List<ScrubberMarker>): List<ReliveChapter> =
        PlaybackBookmarks.fromMarkers(markers).mapIndexed { index, mark ->
            ReliveChapter(
                timestampMs = mark.timestampMs,
                title = mark.label.takeUnless { it.equals("Mark", ignoreCase = true) }
                    ?: "Chapter ${index + 1}",
                sampleIndex = mark.sampleIndex,
            )
        }

    fun fromEvents(events: List<SessionEventEntity>): List<ReliveChapter> =
        events.filter { it.eventType == "mark" }.mapIndexed { index, event ->
            ReliveChapter(
                timestampMs = event.timestampMs,
                title = tagFromPayload(event.payloadJson) ?: "Chapter ${index + 1}",
                sampleIndex = index,
            )
        }

    fun tagFromPayload(json: String?): String? {
        val raw = json ?: return null
        val key = "\"tag\":\""
        val start = raw.indexOf(key)
        if (start < 0) return null
        val from = start + key.length
        val end = raw.indexOf('"', from)
        return if (end > from) raw.substring(from, end) else null
    }
}
