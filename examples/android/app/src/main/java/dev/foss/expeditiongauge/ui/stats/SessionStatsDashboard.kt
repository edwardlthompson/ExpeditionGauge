package dev.foss.expeditiongauge.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.stats.SessionAggregateStats
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SessionStatsDashboard(
    sessions: List<SessionStatsSummary>,
    aggregate: SessionAggregateStats,
    onPlay: (Long) -> Unit,
    onCompare: (Long, Long) -> Unit,
    onExport: (SessionStatsSummary) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.stats_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        StatsAggregateHeader(aggregate = aggregate)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            items(sessions, key = { it.sessionId }) { summary ->
                val compareTarget = sessions.firstOrNull { it.sessionId != summary.sessionId }
                RichSessionCard(
                    summary = summary,
                    onPlay = { onPlay(summary.sessionId) },
                    onCompare = compareTarget?.let { target ->
                        { onCompare(summary.sessionId, target.sessionId) }
                    },
                    onExport = { onExport(summary) },
                )
            }
        }
        if (sessions.size >= 2) {
            Button(
                onClick = { onCompare(sessions[0].sessionId, sessions[1].sessionId) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.stats_compare_latest))
            }
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.stats_back))
        }
    }
}

@Composable
private fun StatsAggregateHeader(aggregate: SessionAggregateStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(SpacingMd), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.stats_aggregate_sessions, aggregate.sessionCount),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(R.string.stats_aggregate_duration, aggregate.totalDurationMs / 1000),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.bodySmall,
            )
            aggregate.bestLapMs?.let { lap ->
                Text(
                    text = stringResource(R.string.stats_aggregate_best_lap, formatLap(lap)),
                    color = GaugeScaleWhite,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
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
