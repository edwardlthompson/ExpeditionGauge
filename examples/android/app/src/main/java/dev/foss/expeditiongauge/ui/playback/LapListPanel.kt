package dev.foss.expeditiongauge.ui.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.timing.LapTimingSummary
import dev.foss.expeditiongauge.timing.PredictiveTimingEngine
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun LapListPanel(
    summary: LapTimingSummary?,
    modifier: Modifier = Modifier,
) {
    if (summary == null || summary.laps.isEmpty()) return
    val engine = PredictiveTimingEngine()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpacingMd)
            .testTag("playback_lap_list"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.lap_list_title),
            color = GaugeYellow,
            style = MaterialTheme.typography.titleMedium,
        )
        summary.sessionBestMs?.let { best ->
            Text(
                text = stringResource(R.string.lap_session_best, engine.formatLapTime(best)),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (summary.theoreticalBestMs > 0) {
            Text(
                text = stringResource(
                    R.string.lap_theoretical_best,
                    engine.formatLapTime(summary.theoreticalBestMs),
                ),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        summary.laps.filter { it.isValid && !it.isOutLap }.forEach { lap ->
            val splits = summary.splitsByLap[lap.id].orEmpty()
            Text(
                text = stringResource(
                    R.string.lap_row,
                    lap.lapNumber,
                    engine.formatLapTime(lap.durationMs),
                ),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.bodySmall,
            )
            splits.forEach { split ->
                Text(
                    text = stringResource(
                        R.string.lap_sector_row,
                        split.sectorIndex + 1,
                        engine.formatLapTime(split.splitMs),
                    ),
                    color = GaugeScaleWhite.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = SpacingMd),
                )
            }
        }
    }
}
