package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.export.PlaybackVideoExportSettings
import dev.foss.expeditiongauge.export.PlaybackVideoExportWorker
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

@Composable
fun PlaybackExportPanel(
    sessionId: Long?,
    selectedPreset: PlaybackVideoExportSettings,
    onPresetSelected: (PlaybackVideoExportSettings) -> Unit,
    onStartExport: () -> Unit,
    onShareExport: (String) -> Unit,
    workInfo: WorkInfo?,
    modifier: Modifier = Modifier,
) {
    val progress = workInfo?.progress?.getInt(PlaybackVideoExportWorker.KEY_PROGRESS, 0) ?: 0
    val outputPath = workInfo?.outputData?.getString(PlaybackVideoExportWorker.KEY_OUTPUT_PATH)
    val isRunning = workInfo?.state == WorkInfo.State.RUNNING || workInfo?.state == WorkInfo.State.ENQUEUED
    val isComplete = workInfo?.state == WorkInfo.State.SUCCEEDED

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("playback_export_panel"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.playback_export_title),
            color = GaugeYellow,
            style = MaterialTheme.typography.titleSmall,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DurationChip("30s", PlaybackVideoExportSettings.PRESET_30S, selectedPreset, onPresetSelected)
            DurationChip("1m", PlaybackVideoExportSettings.PRESET_60S, selectedPreset, onPresetSelected)
            DurationChip("2m", PlaybackVideoExportSettings.PRESET_120S, selectedPreset, onPresetSelected)
        }
        Button(
            onClick = onStartExport,
            enabled = sessionId != null && !isRunning,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("playback_export_start"),
        ) {
            Text(stringResource(R.string.playback_export_start))
        }
        if (isRunning) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("playback_export_progress"),
            )
            Text(
                text = stringResource(R.string.playback_export_progress, progress),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        if (isComplete && outputPath != null) {
            Text(
                text = stringResource(R.string.playback_export_complete),
                color = GaugeYellow,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.testTag("playback_export_complete"),
            )
            Button(
                onClick = { onShareExport(outputPath) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("playback_export_share"),
            ) {
                Text(stringResource(R.string.playback_export_share))
            }
        }
    }
}

@Composable
private fun DurationChip(
    label: String,
    preset: PlaybackVideoExportSettings,
    selected: PlaybackVideoExportSettings,
    onSelected: (PlaybackVideoExportSettings) -> Unit,
) {
    FilterChip(
        selected = selected.clipDurationMs == preset.clipDurationMs,
        onClick = { onSelected(preset) },
        label = { Text(label) },
        modifier = Modifier.testTag("playback_export_duration_$label"),
    )
}
