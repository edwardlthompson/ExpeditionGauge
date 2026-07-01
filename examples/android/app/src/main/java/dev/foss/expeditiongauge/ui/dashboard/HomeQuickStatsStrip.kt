package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.stats.SessionAggregateStats
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun HomeQuickStatsStrip(
    aggregate: SessionAggregateStats,
    modifier: Modifier = Modifier,
) {
    if (aggregate.sessionCount == 0) return

    val totalMinutes = aggregate.totalDurationMs / 60_000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val durationLabel = if (hours > 0) {
        stringResource(R.string.library_stats_duration_hours, hours, minutes)
    } else {
        stringResource(R.string.library_stats_duration_minutes, minutes.coerceAtLeast(1))
    }
    val bestLap = aggregate.bestLapMs?.let { formatLap(it) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpacingMd, vertical = SpacingMd)
            .testTag("home_quick_stats"),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.library_stats_sessions, aggregate.sessionCount),
            color = GaugeYellow,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = durationLabel,
            color = GaugeScaleWhite,
            style = MaterialTheme.typography.labelMedium,
        )
        bestLap?.let { lap ->
            Text(
                text = stringResource(R.string.library_stats_best_lap, lap),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

private fun formatLap(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    val frac = (ms % 1000) / 10
    return if (min > 0) "%d:%02d.%02d".format(min, sec, frac) else "%d.%02d".format(sec, frac)
}
