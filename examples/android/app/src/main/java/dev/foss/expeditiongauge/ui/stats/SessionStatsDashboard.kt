package dev.foss.expeditiongauge.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SessionStatsDashboard(
    sessions: List<SessionStatsSummary>,
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
        LazyColumn(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
            items(sessions, key = { it.sessionId }) { summary ->
                SessionStatsCard(
                    summary = summary,
                    onPlay = { onPlay(summary.sessionId) },
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
private fun SessionStatsCard(
    summary: SessionStatsSummary,
    onPlay: () -> Unit,
    onExport: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(SpacingMd), verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
            Text(text = summary.name, style = MaterialTheme.typography.titleMedium)
            Text(text = stringResource(R.string.stats_duration, summary.durationMs / 1000))
            Text(text = stringResource(R.string.stats_peak_g, summary.peakLatG ?: 0f))
            Text(text = stringResource(R.string.stats_max_beta, summary.maxBetaDeg ?: 0f))
            Text(text = stringResource(R.string.stats_slip_events, summary.slipEventCount))
            Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
                Button(onClick = onPlay) { Text(stringResource(R.string.stats_play)) }
                Button(onClick = onExport) { Text(stringResource(R.string.stats_export)) }
            }
        }
    }
}
