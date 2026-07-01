package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

/**
 * Split top chrome: menu (start) and record controls (end) overlay the HUD
 * so cube tiles can use the full height below the status bar.
 */
@Composable
fun DashboardHudTopChrome(
    recording: Boolean,
    isLive: Boolean,
    onMenuClick: () -> Unit,
    onRecordClick: () -> Unit,
    onMarkEvent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.testTag("dashboard_menu"),
        ) {
            Icon(
                Icons.Filled.Menu,
                contentDescription = stringResource(R.string.dashboard_menu_open),
                tint = GaugeYellow,
            )
        }
        when {
            isLive -> Text(
                text = stringResource(R.string.live_banner),
                color = GaugeYellow,
                style = MaterialTheme.typography.labelMedium,
            )
            recording -> Text(
                text = stringResource(R.string.recording_live),
                color = GaugeRed,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (recording && FeatureFlags.markEventEnabled) {
                IconButton(
                    onClick = onMarkEvent,
                    modifier = Modifier.testTag("mark_event"),
                ) {
                    Icon(
                        Icons.Filled.Flag,
                        contentDescription = stringResource(R.string.mark_event_fab),
                        tint = GaugeYellow,
                    )
                }
            }
            IconButton(
                onClick = onRecordClick,
                modifier = Modifier.testTag(if (recording) "record_stop" else "record_play"),
            ) {
                Icon(
                    imageVector = if (recording) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                    contentDescription = stringResource(
                        if (recording) R.string.recording_stop else R.string.recording_start,
                    ),
                    tint = if (recording) GaugeRed else GaugeYellow,
                )
            }
        }
    }
}
