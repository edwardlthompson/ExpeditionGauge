package dev.foss.expeditiongauge.ui.playbackbookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.playbackbookmarks.PlaybackBookmarks
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun PlaybackBookmarkList(
    markers: List<ScrubberMarker>,
    onJump: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bookmarks = PlaybackBookmarks.fromMarkers(markers)
    if (bookmarks.isEmpty()) return
    LazyRow(
        modifier = modifier.testTag("playback_bookmark_list"),
        horizontalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        items(bookmarks, key = { it.sampleIndex }) { mark ->
            TextButton(onClick = { onJump(mark.sampleIndex) }) {
                Text(mark.label.ifBlank { stringResource(R.string.playback_bookmark_mark) })
            }
        }
    }
}
