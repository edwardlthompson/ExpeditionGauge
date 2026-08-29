package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.feedback.FeedbackPrefs
import dev.foss.expeditiongauge.ui.liveencrypt.LiveEncryptField
import dev.foss.expeditiongauge.ui.livemultireceiver.LiveMultiReceiverLabel
import dev.foss.expeditiongauge.ui.privacyreportexport.PrivacyReportExportButton
import dev.foss.expeditiongauge.ui.settingsjsonbackup.SettingsBackupButton
import dev.foss.expeditiongauge.ui.saffolderpicker.SafFolderButton
import dev.foss.expeditiongauge.ui.settingsqrtransfer.SettingsQrLabel

@Composable
internal fun SettingsAdvancedCategory(
    state: SettingsUiState,
    actions: SettingsUiActions,
) {
    SettingsSwitchRow(
        label = stringResource(R.string.developer_mode_enable),
        checked = state.developerModeEnabled,
        onCheckedChange = actions.onDeveloperModeChange,
        modifier = Modifier.testTag("settings_developer_mode"),
    )
    if (state.developerModeEnabled) {
        Button(onClick = actions.onDeveloperModeOpen, modifier = Modifier.testTag("settings_developer_open")) {
            Text(stringResource(R.string.developer_mode_open))
        }
    }
    SettingsSwitchRow(
        label = stringResource(R.string.settings_live_telemetry),
        checked = state.liveTelemetryEnabled,
        onCheckedChange = actions.onLiveTelemetryChange,
        modifier = Modifier.testTag("settings_live_telemetry"),
    )
    if (state.liveTelemetryEnabled) {
        OutlinedTextField(
            value = state.liveSignalWssUrl,
            onValueChange = actions.onLiveSignalWssUrlChange,
            label = { Text(stringResource(R.string.live_signal_url_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_live_signal_url"),
            singleLine = true,
        )
        Button(onClick = actions.onLiveReceiverOpen, modifier = Modifier.testTag("settings_live_receiver")) {
            Text(stringResource(R.string.live_receiver_open))
        }
        LiveEncryptField()
        LiveMultiReceiverLabel(count = 0)
    }
    SettingsSwitchRow(
        label = stringResource(R.string.settings_update_check_label),
        checked = state.updateCheckEnabled,
        onCheckedChange = actions.onUpdateCheckChange,
        modifier = Modifier.testTag("settings_update_check"),
    )
    val context = LocalContext.current
    val prefs = remember { FeedbackPrefs(context) }
    var saveCrashes by remember { mutableStateOf(prefs.saveCrashes()) }
    SettingsSwitchRow(
        label = stringResource(R.string.settings_save_crashes),
        checked = saveCrashes,
        onCheckedChange = {
            prefs.setSaveCrashes(it)
            saveCrashes = it
        },
        modifier = Modifier.testTag("settings_save_crashes"),
    )
    PrivacyReportExportButton()
    SettingsBackupButton()
    SettingsQrLabel()
}
