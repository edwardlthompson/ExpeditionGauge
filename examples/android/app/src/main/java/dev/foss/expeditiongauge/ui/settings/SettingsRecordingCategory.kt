package dev.foss.expeditiongauge.ui.settings

import androidx.compose.runtime.Composable
import dev.foss.expeditiongauge.ui.batterysaverrecord.BatterySaverRecordField
import dev.foss.expeditiongauge.ui.csvcolumns.CsvColumnPickerField

@Composable
internal fun SettingsRecordingCategory(
    state: SettingsUiState,
    actions: SettingsUiActions,
) {
    BatterySaverRecordField()
    CsvColumnPickerField()
    SettingsRecordingRateOptions(
        logIntervalMs = state.logIntervalMs,
        onLogIntervalSelect = actions.onLogIntervalSelect,
        recordingMode = state.recordingMode,
        onRecordingModeSelect = actions.onRecordingModeSelect,
    )
    SettingsLapTimingOptions(
        lapTimingEnabled = state.lapTimingEnabled,
        onLapTimingEnabledChange = actions.onLapTimingEnabledChange,
        onTrackSetup = actions.onTrackSetup,
    )
    SettingsMediaOptions(
        compressionQuality = state.mediaCompressionQuality,
        onCompressionSelect = actions.onMediaCompressionSelect,
        storageBytes = state.mediaStorageBytes,
    )
    SettingsStorageOptions(
        storagePercent = state.sessionStoragePercent,
        usedBytes = state.sessionStorageUsedBytes,
        allowedBytes = state.sessionStorageAllowedBytes,
        onPercentChange = actions.onSessionStoragePercentChange,
    )
    SettingsRecordingOptions(
        autoRecordEnabled = state.autoRecordEnabled,
        selectedAddresses = state.autoRecordDeviceAddresses,
        onAutoRecordEnabledChange = actions.onAutoRecordEnabledChange,
        onDeviceToggle = actions.onAutoRecordDeviceToggle,
    )
}
