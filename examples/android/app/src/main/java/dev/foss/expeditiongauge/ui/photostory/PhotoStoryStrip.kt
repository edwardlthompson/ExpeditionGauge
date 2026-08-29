package dev.foss.expeditiongauge.ui.photostory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.photostory.PhotoStoryTimeline
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun PhotoStoryStrip(
    markers: List<ScrubberMarker>,
    onJump: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val photos = PhotoStoryTimeline.fromMarkers(markers)
    if (photos.isEmpty()) return
    LazyRow(
        modifier = modifier.testTag("photo_story_strip"),
        horizontalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        items(photos, key = { it.mediaId ?: it.timestampMs }) { photo ->
            TextButton(onClick = { onJump(photo.sampleIndex) }) {
                Text(photo.label)
            }
        }
    }
}
