package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.video.VideoSyncEngine

@Composable
fun PlaybackVideoControls(
    videoSyncEngine: VideoSyncEngine,
    hasVideo: Boolean,
    videoOffsetMs: Long,
    onImportVideo: () -> Unit,
    onExportBurnIn: () -> Unit,
    onVideoOffsetChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("playback_video_controls"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onImportVideo, modifier = Modifier.testTag("playback_video_import")) {
                Text(stringResource(R.string.video_sync_import))
            }
            if (hasVideo) {
                Button(onClick = onExportBurnIn, modifier = Modifier.testTag("playback_video_burn_in")) {
                    Text(stringResource(R.string.video_sync_burn_in))
                }
            }
        }
        if (hasVideo) {
            PlaybackVideoPanel(videoSyncEngine = videoSyncEngine)
            Text(
                text = stringResource(R.string.video_sync_offset, videoOffsetMs.toInt()),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.testTag("playback_video_offset_label"),
            )
            Slider(
                value = videoOffsetMs.toFloat(),
                onValueChange = { onVideoOffsetChange(it.toLong()) },
                valueRange = -5000f..5000f,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("playback_video_offset_slider"),
            )
        } else {
            Text(
                text = stringResource(R.string.video_sync_no_video),
                color = GaugeYellow,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
