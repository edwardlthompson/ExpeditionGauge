package dev.foss.expeditiongauge.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.sessionmapcompare.SessionMapCompare
import dev.foss.expeditiongauge.stats.SessionComparison
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SessionComparisonScreen(
    comparison: SessionComparison,
    onExport: () -> Unit,
    onGhostCompare: (() -> Unit)? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .testTag("session_comparison"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.comparison_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        ComparisonRow(
            label = stringResource(R.string.comparison_peak_g),
            left = comparison.left.peakLatG,
            right = comparison.right.peakLatG,
            delta = comparison.peakGDelta,
        )
        ComparisonRow(
            label = stringResource(R.string.comparison_max_beta),
            left = comparison.left.maxBetaDeg,
            right = comparison.right.maxBetaDeg,
            delta = null,
        )
        ComparisonLapRow(
            leftMs = comparison.left.bestLapMs,
            rightMs = comparison.right.bestLapMs,
            deltaMs = comparison.bestLapDeltaMs,
        )
        Text(text = stringResource(R.string.comparison_slip_delta, comparison.slipDelta))
        val leftPts = SessionMapCompare.fromThumb(comparison.left.routeThumb)
        val rightPts = SessionMapCompare.fromThumb(comparison.right.routeThumb)
        Text(
            text = stringResource(R.string.session_map_compare_points, leftPts.size, rightPts.size),
            modifier = Modifier.testTag("session_map_compare_points"),
        )
        if (onGhostCompare != null && dev.foss.expeditiongauge.FeatureFlags.ghostLapEnabled) {
            Button(onClick = onGhostCompare, modifier = Modifier.testTag("comparison_ghost_map")) {
                Text(stringResource(R.string.comparison_ghost_map))
            }
        }
        Button(onClick = onExport) {
            Text(stringResource(R.string.stats_export))
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.stats_back))
        }
    }
}

@Composable
private fun ComparisonLapRow(leftMs: Long?, rightMs: Long?, deltaMs: Long?) {
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Text(text = stringResource(R.string.comparison_best_lap), style = MaterialTheme.typography.titleSmall)
        Text(
            text = stringResource(
                R.string.comparison_lap_side_by_side,
                leftMs?.let { formatLap(it) } ?: "—",
                rightMs?.let { formatLap(it) } ?: "—",
            ),
        )
        deltaMs?.let {
            Text(text = stringResource(R.string.comparison_lap_delta_ms, it))
        }
    }
}

@Composable
private fun ComparisonRow(
    label: String,
    left: Float?,
    right: Float?,
    delta: Float?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Text(text = label, style = MaterialTheme.typography.titleSmall)
        Text(text = stringResource(R.string.comparison_side_by_side, left ?: 0f, right ?: 0f))
        delta?.let {
            Text(text = stringResource(R.string.comparison_delta, it))
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
