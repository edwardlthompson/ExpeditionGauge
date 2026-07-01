package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.playback.PlaybackEngine
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun PlaybackDriftAnalysisToggle(
    showDriftAnalysis: Boolean,
    engine: PlaybackEngine,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Drift Analysis" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(stringResource(R.string.playback_drift_analysis), color = GaugeScaleWhite)
        Switch(
            checked = showDriftAnalysis,
            onCheckedChange = { engine.toggleDriftAnalysis() },
            modifier = Modifier.testTag("playback_drift_toggle"),
        )
    }
}
