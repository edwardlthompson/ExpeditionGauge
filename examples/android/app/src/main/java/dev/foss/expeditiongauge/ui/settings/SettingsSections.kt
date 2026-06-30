package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.recording.RecordingMode
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.ui.theme.BrightnessMode
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import dev.foss.expeditiongauge.ui.theme.ThemeMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SettingsGeneralSections(
    themeMode: ThemeMode,
    onThemeModeSelect: (ThemeMode) -> Unit,
    brightnessMode: BrightnessMode,
    onBrightnessModeSelect: (BrightnessMode) -> Unit,
    speedUnit: SpeedUnit,
    onSpeedUnitSelect: (SpeedUnit) -> Unit,
    logIntervalMs: Long,
    onLogIntervalSelect: (Long) -> Unit,
    recordingMode: RecordingMode,
    onRecordingModeSelect: (RecordingMode) -> Unit,
) {
    Text(text = stringResource(R.string.settings_theme_label))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
        ThemeMode.entries.forEach { mode ->
            FilterChip(
                selected = themeMode == mode,
                onClick = { onThemeModeSelect(mode) },
                label = {
                    Text(
                        when (mode) {
                            ThemeMode.System -> stringResource(R.string.settings_theme_mode_system)
                            ThemeMode.Light -> stringResource(R.string.settings_theme_mode_light)
                            ThemeMode.Dark -> stringResource(R.string.settings_theme_mode_dark)
                        },
                    )
                },
            )
        }
    }
    Text(text = stringResource(R.string.settings_brightness_label))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
        BrightnessMode.entries.forEach { mode ->
            FilterChip(
                selected = brightnessMode == mode,
                onClick = { onBrightnessModeSelect(mode) },
                label = {
                    Text(
                        when (mode) {
                            BrightnessMode.Auto -> stringResource(R.string.settings_brightness_auto)
                            BrightnessMode.Day -> stringResource(R.string.settings_brightness_day)
                            BrightnessMode.Night -> stringResource(R.string.settings_brightness_night)
                        },
                    )
                },
            )
        }
    }
    Text(text = stringResource(R.string.settings_units_label))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
        SpeedUnit.entries.forEach { unit ->
            FilterChip(
                selected = speedUnit == unit,
                onClick = { onSpeedUnitSelect(unit) },
                label = {
                    Text(
                        when (unit) {
                            SpeedUnit.METRIC -> stringResource(R.string.settings_unit_metric)
                            SpeedUnit.IMPERIAL -> stringResource(R.string.settings_unit_imperial)
                        },
                    )
                },
            )
        }
    }
    Text(text = stringResource(R.string.settings_log_rate_label))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
        listOf(20L, 50L, 100L).forEach { rate ->
            FilterChip(
                selected = logIntervalMs == rate,
                onClick = { onLogIntervalSelect(rate) },
                label = { Text(stringResource(R.string.settings_log_rate_hz, 1000 / rate)) },
            )
        }
    }
    Text(
        text = stringResource(R.string.settings_performance_hint),
        style = MaterialTheme.typography.bodySmall,
    )
    if (FeatureFlags.crawlingModeEnabled) {
        Text(text = stringResource(R.string.settings_recording_mode_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            FilterChip(
                selected = recordingMode == RecordingMode.NORMAL,
                onClick = { onRecordingModeSelect(RecordingMode.NORMAL) },
                label = { Text(stringResource(R.string.recording_mode_normal)) },
                modifier = Modifier.testTag("settings_recording_mode_normal"),
            )
            FilterChip(
                selected = recordingMode == RecordingMode.CRAWLING,
                onClick = { onRecordingModeSelect(RecordingMode.CRAWLING) },
                label = { Text(stringResource(R.string.recording_mode_crawl)) },
                modifier = Modifier.testTag("settings_recording_mode_crawl"),
            )
        }
    }
}
