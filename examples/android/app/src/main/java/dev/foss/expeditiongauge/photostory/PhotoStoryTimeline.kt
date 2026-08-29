package dev.foss.expeditiongauge.photostory

import dev.foss.expeditiongauge.data.db.entities.SessionMediaEntity
import dev.foss.expeditiongauge.data.db.entities.SessionMediaKind
import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.playback.ScrubberMarkerType

data class PhotoStoryItem(
    val mediaId: Long?,
    val timestampMs: Long,
    val label: String,
    val sampleIndex: Int,
)

/** Horizontal Relive photo story from media markers or Room photos. */
object PhotoStoryTimeline {
    fun fromMarkers(markers: List<ScrubberMarker>): List<PhotoStoryItem> =
        markers.filter { it.type == ScrubberMarkerType.MEDIA_ATTACHMENT }
            .sortedBy { it.timestampMs }
            .map {
                PhotoStoryItem(it.mediaId, it.timestampMs, it.label ?: "Photo", it.sampleIndex)
            }

    fun fromMedia(media: List<SessionMediaEntity>): List<PhotoStoryItem> =
        media.filter { it.mediaKind == SessionMediaKind.PHOTO }
            .sortedBy { it.timestampMs }
            .map { PhotoStoryItem(it.id, it.timestampMs, it.fileName, 0) }
}
