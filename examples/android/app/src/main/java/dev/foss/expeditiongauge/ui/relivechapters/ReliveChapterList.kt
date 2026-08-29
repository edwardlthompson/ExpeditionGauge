package dev.foss.expeditiongauge.ui.relivechapters

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.playback.ScrubberMarker
import dev.foss.expeditiongauge.relivechapters.ReliveChapters
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite

@Composable
fun ReliveChapterList(
    markers: List<ScrubberMarker>,
    onJump: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chapters = ReliveChapters.fromMarkers(markers)
    if (chapters.isEmpty()) return
    Column(modifier = modifier.testTag("relive_chapter_list")) {
        Text(text = stringResource(R.string.relive_chapters_title), color = GaugeScaleWhite)
        chapters.forEach { chapter ->
            TextButton(onClick = { onJump(chapter.sampleIndex) }) {
                Text(chapter.title)
            }
        }
    }
}
