package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.display.highRefreshScroll
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
internal fun SettingsCategoryPane(
    category: SettingsCategory,
    state: SettingsUiState,
    actions: SettingsUiActions,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .highRefreshScroll(),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = categoryTitle(category),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        Button(onClick = onBack, modifier = Modifier.testTag("settings_category_back")) {
            Text(stringResource(R.string.dashboard_menu_back))
        }
        when (category) {
            SettingsCategory.Display -> SettingsDisplayCategory(state, actions)
            SettingsCategory.Recording -> SettingsRecordingCategory(state, actions)
            SettingsCategory.Alerts -> SettingsAccessibilityOptions(
                thresholds = state.alertThresholds,
                onThresholdsChange = actions.onAlertThresholdsChange,
                speedUnit = state.speedUnit,
                pressureUnit = state.pressureUnit,
                alertAudioMode = state.alertAudioMode,
                onAlertAudioModeChange = actions.onAlertAudioModeChange,
                alertsMuted = state.alertsMuted,
                onAlertsMutedChange = actions.onAlertsMutedChange,
                highContrastEnabled = state.highContrastEnabled,
                onHighContrastChange = actions.onHighContrastChange,
                largeTextEnabled = state.largeTextEnabled,
                onLargeTextChange = actions.onLargeTextChange,
                ttsReadoutEnabled = state.ttsReadoutEnabled,
                onTtsReadoutChange = actions.onTtsReadoutChange,
                audibleTonesEnabled = state.audibleTonesEnabled,
                onAudibleTonesChange = actions.onAudibleTonesChange,
            )
            SettingsCategory.Hardware -> SettingsHardwareCategory(state, actions)
            SettingsCategory.Maps -> {
                SettingsAndroidAutoOptions()
                SettingsMapOptions(
                    homeRegion = state.homeMapRegion,
                    onUseCurrentLocation = actions.onUseCurrentLocationAsHomeRegion,
                )
            }
            SettingsCategory.Advanced -> SettingsAdvancedCategory(state, actions)
        }
    }
}

@Composable
private fun categoryTitle(category: SettingsCategory): String = when (category) {
    SettingsCategory.Display -> stringResource(R.string.settings_hub_display)
    SettingsCategory.Recording -> stringResource(R.string.settings_hub_recording)
    SettingsCategory.Alerts -> stringResource(R.string.settings_hub_alerts)
    SettingsCategory.Hardware -> stringResource(R.string.settings_hub_hardware)
    SettingsCategory.Maps -> stringResource(R.string.settings_hub_maps)
    SettingsCategory.Advanced -> stringResource(R.string.settings_hub_advanced)
}
