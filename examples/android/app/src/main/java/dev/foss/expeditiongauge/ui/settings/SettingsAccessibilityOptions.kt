package dev.foss.expeditiongauge.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.alerts.AlertAudioMode
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit

@Composable
fun SettingsAccessibilityOptions(
    thresholds: AlertThresholds,
    onThresholdsChange: (AlertThresholds) -> Unit,
    speedUnit: SpeedUnit,
    pressureUnit: PressureUnit,
    alertAudioMode: AlertAudioMode,
    onAlertAudioModeChange: (AlertAudioMode) -> Unit,
    alertsMuted: Boolean,
    onAlertsMutedChange: (Boolean) -> Unit,
    highContrastEnabled: Boolean,
    onHighContrastChange: (Boolean) -> Unit,
    largeTextEnabled: Boolean,
    onLargeTextChange: (Boolean) -> Unit,
    ttsReadoutEnabled: Boolean,
    onTtsReadoutChange: (Boolean) -> Unit,
    audibleTonesEnabled: Boolean,
    onAudibleTonesChange: (Boolean) -> Unit,
) {
    SettingsAlertOptions(
        thresholds = thresholds,
        onThresholdsChange = onThresholdsChange,
        speedUnit = speedUnit,
        pressureUnit = pressureUnit,
        alertAudioMode = alertAudioMode,
        onAlertAudioModeChange = onAlertAudioModeChange,
        alertsMuted = alertsMuted,
        onAlertsMutedChange = onAlertsMutedChange,
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_high_contrast),
        checked = highContrastEnabled,
        onCheckedChange = onHighContrastChange,
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_large_text),
        checked = largeTextEnabled,
        onCheckedChange = onLargeTextChange,
        modifier = Modifier.testTag("settings_large_text"),
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_tts_readout),
        checked = ttsReadoutEnabled,
        onCheckedChange = onTtsReadoutChange,
        modifier = Modifier.testTag("settings_tts_readout"),
    )
    SettingsSwitchRow(
        label = stringResource(R.string.settings_audible_tones),
        checked = audibleTonesEnabled,
        onCheckedChange = onAudibleTonesChange,
    )
}
