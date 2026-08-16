package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun DrawerLibraryPage(
    recording: Boolean,
    onSessionsOpen: () -> Unit,
    onStatsOpen: () -> Unit,
    onRecordingOptions: () -> Unit,
    onBack: () -> Unit,
) {
    Column {
        DrawerMenuItem(
            label = stringResource(R.string.dashboard_menu_back),
            onClick = onBack,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            testTag = "drawer_back",
        )
        Text(
            text = stringResource(R.string.dashboard_menu_library),
            color = GaugeYellow,
            modifier = Modifier.padding(vertical = SpacingMd),
        )
        DrawerMenuItem(
            label = stringResource(R.string.playback_sessions),
            onClick = onSessionsOpen,
            icon = Icons.Filled.Folder,
            testTag = "drawer_sessions",
        )
        DrawerMenuItem(
            label = stringResource(R.string.stats_open),
            onClick = onStatsOpen,
            icon = Icons.Filled.BarChart,
            testTag = "drawer_stats",
        )
        if (recording) {
            DrawerMenuItem(
                label = stringResource(R.string.recording_advanced_open),
                onClick = onRecordingOptions,
                icon = Icons.Filled.Tune,
                testTag = "drawer_recording_options",
            )
        }
    }
}
