package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun DrawerRootPage(
    recording: Boolean,
    isLive: Boolean,
    showPreset: Boolean,
    showLive: Boolean,
    activePresetId: DashboardPresetId,
    onRecordClick: () -> Unit,
    onPresetOpen: () -> Unit,
    onLibraryOpen: () -> Unit,
    onImuManage: () -> Unit,
    onLiveClick: () -> Unit,
    onSettingsOpen: () -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.dashboard_menu_title),
            color = GaugeYellow,
            modifier = Modifier.padding(bottom = SpacingMd),
        )
        DrawerMenuItem(
            label = stringResource(
                if (recording) R.string.recording_stop else R.string.recording_start,
            ),
            onClick = onRecordClick,
            icon = if (recording) Icons.Filled.Stop else Icons.Filled.PlayArrow,
            testTag = if (recording) "menu_record_stop" else "menu_record_start",
        )
        if (showPreset) {
            DrawerMenuItem(
                label = stringResource(R.string.dashboard_menu_preset),
                onClick = onPresetOpen,
                icon = Icons.AutoMirrored.Filled.ViewQuilt,
                supportingText = presetDrawerLabel(activePresetId),
                trailingChevron = true,
                testTag = "drawer_preset_open",
            )
        }
        DrawerMenuItem(
            label = stringResource(R.string.dashboard_menu_library),
            onClick = onLibraryOpen,
            icon = Icons.Filled.Folder,
            supportingText = stringResource(R.string.dashboard_menu_library_hint),
            trailingChevron = true,
            testTag = "drawer_library",
        )
        DrawerMenuItem(
            label = stringResource(R.string.imu_management_title),
            onClick = onImuManage,
            icon = Icons.Filled.Sensors,
            testTag = "drawer_imu",
        )
        if (showLive) {
            DrawerMenuItem(
                label = stringResource(if (isLive) R.string.live_stop else R.string.live_start),
                onClick = onLiveClick,
                icon = Icons.Filled.Wifi,
                testTag = if (isLive) "drawer_live_stop" else "drawer_live_start",
            )
        }
        DrawerMenuItem(
            label = stringResource(R.string.settings_open),
            onClick = onSettingsOpen,
            icon = Icons.Filled.Settings,
            testTag = "drawer_settings",
        )
    }
}
