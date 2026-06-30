package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.playback.PlaybackState
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlaybackOverlayControls(
    state: PlaybackState,
    onToggleRoute: () -> Unit,
    onToggleDrivingLine: () -> Unit,
    onToggleGhost: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!FeatureFlags.drivingLineEnabled && !FeatureFlags.ghostLapEnabled) return
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpacingMd)
            .testTag("playback_overlay_controls"),
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        if (FeatureFlags.drivingLineEnabled) {
            FilterChip(
                selected = state.showRoute,
                onClick = onToggleRoute,
                label = { Text(stringResource(R.string.playback_overlay_route)) },
                modifier = Modifier.testTag("playback_overlay_route"),
            )
            FilterChip(
                selected = state.showDrivingLine,
                onClick = onToggleDrivingLine,
                label = { Text(stringResource(R.string.playback_overlay_driving_line)) },
                modifier = Modifier.testTag("playback_overlay_driving_line"),
            )
        }
        if (FeatureFlags.ghostLapEnabled) {
            FilterChip(
                selected = state.showGhost,
                onClick = onToggleGhost,
                label = { Text(stringResource(R.string.playback_overlay_ghost)) },
                modifier = Modifier.testTag("playback_overlay_ghost"),
            )
        }
    }
}
