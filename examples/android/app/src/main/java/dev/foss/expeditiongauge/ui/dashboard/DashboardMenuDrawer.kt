package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.ui.components.ThemeToggle
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import dev.foss.expeditiongauge.ui.theme.ThemeMode

@Composable
fun DashboardMenuDrawer(
    drawerOpen: Boolean,
    onDrawerOpenChange: (Boolean) -> Unit,
    recording: Boolean,
    isLive: Boolean,
    liveTelemetryEnabled: Boolean,
    activePresetId: DashboardPresetId,
    themeMode: ThemeMode,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onSessionsOpen: () -> Unit,
    onRecordingOptions: () -> Unit,
    onStatsOpen: () -> Unit,
    onPresetSelected: (DashboardPresetId) -> Unit,
    onImuManage: () -> Unit,
    onStartLive: () -> Unit,
    onStopLive: () -> Unit,
    onSettingsOpen: () -> Unit,
    onAboutOpen: () -> Unit,
    onThemeToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    LaunchedEffect(drawerOpen) {
        if (drawerOpen && !drawerState.isOpen) drawerState.open()
        if (!drawerOpen && drawerState.isOpen) drawerState.close()
    }
    LaunchedEffect(drawerState.isOpen) {
        if (!drawerState.isOpen && drawerOpen) onDrawerOpenChange(false)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxHeight()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(SpacingMd)
                        .testTag("dashboard_drawer"),
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_menu_title),
                        color = GaugeYellow,
                        modifier = Modifier.padding(bottom = SpacingMd),
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(
                                if (recording) {
                                    stringResource(R.string.recording_stop)
                                } else {
                                    stringResource(R.string.recording_start)
                                },
                            )
                        },
                        selected = false,
                        onClick = {
                            onDrawerOpenChange(false)
                            if (recording) onStopRecording() else onStartRecording()
                        },
                        modifier = Modifier.testTag(
                            if (recording) "menu_record_stop" else "menu_record_start",
                        ),
                    )
                    DrawerNavItem(R.string.playback_sessions, onSessionsOpen) { onDrawerOpenChange(false) }
                    if (recording) {
                        DrawerNavItem(R.string.recording_advanced_open, onRecordingOptions) {
                            onDrawerOpenChange(false)
                        }
                    }
                    DrawerNavItem(R.string.stats_open, onStatsOpen) { onDrawerOpenChange(false) }
                    if (FeatureFlags.dashboardPresetsEnabled) {
                        Text(
                            text = stringResource(R.string.dashboard_menu_preset),
                            color = GaugeYellow,
                            modifier = Modifier.padding(top = SpacingMd),
                        )
                        DashboardPreset.all.forEach { preset ->
                            PresetDrawerRow(
                                presetId = preset.id,
                                selected = activePresetId == preset.id,
                                onSelect = {
                                    onPresetSelected(preset.id)
                                    onDrawerOpenChange(false)
                                },
                            )
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = SpacingMd))
                    DrawerNavItem(R.string.imu_management_title, onImuManage) { onDrawerOpenChange(false) }
                    if (liveTelemetryEnabled && FeatureFlags.liveTelemetryEnabled) {
                        DrawerNavItem(
                            labelRes = if (isLive) R.string.live_stop else R.string.live_start,
                            onClick = {
                                onDrawerOpenChange(false)
                                if (isLive) onStopLive() else onStartLive()
                            },
                        )
                    }
                    DrawerNavItem(R.string.settings_open, onSettingsOpen) { onDrawerOpenChange(false) }
                    DrawerNavItem(R.string.about_open, onAboutOpen) { onDrawerOpenChange(false) }
                    ThemeToggle(
                        themeMode = themeMode,
                        onToggle = {
                            onThemeToggle()
                            onDrawerOpenChange(false)
                        },
                        modifier = Modifier.padding(top = SpacingMd),
                    )
                }
            }
        },
        content = content,
    )
}

@Composable
private fun DrawerNavItem(
    labelRes: Int,
    onClick: () -> Unit,
    onClose: () -> Unit = {},
) {
    NavigationDrawerItem(
        label = { Text(stringResource(labelRes)) },
        selected = false,
        onClick = {
            onClose()
            onClick()
        },
    )
}

@Composable
private fun PresetDrawerRow(
    presetId: DashboardPresetId,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = SpacingMd / 2)
            .testTag("drawer_preset_${presetId.name.lowercase()}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = presetDrawerLabel(presetId))
    }
}

@Composable
private fun presetDrawerLabel(id: DashboardPresetId): String = when (id) {
    DashboardPresetId.Default -> stringResource(R.string.preset_default)
    DashboardPresetId.Drift -> stringResource(R.string.preset_drift)
    DashboardPresetId.Offroad -> stringResource(R.string.preset_offroad)
    DashboardPresetId.Track -> stringResource(R.string.preset_track)
    DashboardPresetId.Minimal -> stringResource(R.string.preset_minimal)
}
