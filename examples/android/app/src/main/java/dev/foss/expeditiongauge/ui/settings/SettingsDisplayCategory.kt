package dev.foss.expeditiongauge.ui.settings

import androidx.compose.runtime.Composable

@Composable
internal fun SettingsDisplayCategory(
    state: SettingsUiState,
    actions: SettingsUiActions,
) {
    SettingsDisplayChrome(
        themeMode = state.themeMode,
        onThemeModeSelect = actions.onThemeModeSelect,
        brightnessMode = state.brightnessMode,
        onBrightnessModeSelect = actions.onBrightnessModeSelect,
        keepScreenAwake = state.keepScreenAwake,
        onKeepScreenAwakeChange = actions.onKeepScreenAwakeChange,
        speedUnit = state.speedUnit,
        onSpeedUnitSelect = actions.onSpeedUnitSelect,
    )
    SettingsPresetOptions(
        activePresetId = state.activePresetId,
        onPresetSelected = actions.onPresetSelected,
    )
    SettingsPolishOptions(
        attitudeGaugeMode = state.attitudeGaugeMode,
        onAttitudeGaugeModeSelect = actions.onAttitudeGaugeModeSelect,
    )
    SettingsScreenshotOptions(
        screenshotMode = state.screenshotMode,
        onScreenshotModeSelected = actions.onScreenshotModeSelected,
    )
}
