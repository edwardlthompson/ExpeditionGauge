package dev.foss.expeditiongauge.ui.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.ui.theme.GaugeBackground
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun DashboardMenuDrawer(
    drawerOpen: Boolean,
    onDrawerOpenChange: (Boolean) -> Unit,
    recording: Boolean,
    isLive: Boolean,
    liveTelemetryEnabled: Boolean,
    activePresetId: DashboardPresetId,
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
    content: @Composable () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var page by remember { mutableStateOf(DrawerPage.Root) }
    LaunchedEffect(drawerOpen) {
        if (drawerOpen && !drawerState.isOpen) drawerState.open()
        if (!drawerOpen && drawerState.isOpen) drawerState.close()
        if (!drawerOpen) page = drawerPageAfterClosed()
    }
    LaunchedEffect(drawerState.isOpen) {
        if (!drawerState.isOpen && drawerOpen) onDrawerOpenChange(false)
    }
    BackHandler(enabled = drawerOpen) {
        when (drawerBackTarget(page)) {
            DrawerBackTarget.Close -> onDrawerOpenChange(false)
            DrawerBackTarget.Root -> page = DrawerPage.Root
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(min = 280.dp, max = 360.dp),
                drawerContainerColor = GaugeBackground,
                drawerContentColor = GaugeScaleWhite,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(SpacingMd)
                        .testTag("dashboard_drawer"),
                ) {
                    when (page) {
                        DrawerPage.Root -> DrawerRootPage(
                            recording = recording,
                            isLive = isLive,
                            showPreset = drawerShowsPreset(FeatureFlags.dashboardPresetsEnabled),
                            showLive = drawerShowsLive(
                                liveTelemetryEnabled,
                                FeatureFlags.liveTelemetryEnabled,
                            ),
                            activePresetId = activePresetId,
                            onRecordClick = {
                                onDrawerOpenChange(false)
                                if (recording) onStopRecording() else onStartRecording()
                            },
                            onPresetOpen = { page = DrawerPage.Preset },
                            onLibraryOpen = { page = DrawerPage.Library },
                            onImuManage = {
                                onDrawerOpenChange(false)
                                onImuManage()
                            },
                            onLiveClick = {
                                onDrawerOpenChange(false)
                                if (isLive) onStopLive() else onStartLive()
                            },
                            onSettingsOpen = {
                                onDrawerOpenChange(false)
                                onSettingsOpen()
                            },
                        )
                        DrawerPage.Preset -> DrawerPresetPage(
                            activePresetId = activePresetId,
                            onPresetSelected = { id ->
                                onPresetSelected(id)
                                onDrawerOpenChange(false)
                            },
                            onBack = { page = DrawerPage.Root },
                        )
                        DrawerPage.Library -> DrawerLibraryPage(
                            recording = recording,
                            onSessionsOpen = {
                                onDrawerOpenChange(false)
                                onSessionsOpen()
                            },
                            onStatsOpen = {
                                onDrawerOpenChange(false)
                                onStatsOpen()
                            },
                            onRecordingOptions = {
                                onDrawerOpenChange(false)
                                onRecordingOptions()
                            },
                            onBack = { page = DrawerPage.Root },
                        )
                    }
                }
            }
        },
        content = content,
    )
}
