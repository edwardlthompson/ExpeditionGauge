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
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.stats.SessionComparison
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SessionComparisonScreen(
    comparison: SessionComparison,
    onExport: () -> Unit,
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
        Text(text = stringResource(R.string.comparison_slip_delta, comparison.slipDelta))
        Button(onClick = onExport) {
            Text(stringResource(R.string.stats_export))
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.stats_back))
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
