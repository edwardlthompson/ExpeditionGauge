package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.telemetry.ImuStatusEntry
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun ImuStatusStrip(
    statuses: List<ImuStatusEntry>,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (statuses.isEmpty()) {
        Text(
            text = stringResource(R.string.imu_none_connected),
            color = GaugeYellow.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodySmall,
            modifier = modifier
                .clickable(onClick = onManageClick)
                .padding(SpacingSm),
        )
        return
    }
    Row(
        modifier = modifier
            .clickable(onClick = onManageClick)
            .padding(SpacingSm),
        horizontalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        statuses.forEach { status ->
            val color = when (status.signalQuality) {
                "Good" -> GaugeYellow
                "Fair" -> GaugeYellow.copy(alpha = 0.7f)
                "Poor" -> GaugeYellow.copy(alpha = 0.4f)
                else -> GaugeYellow.copy(alpha = 0.3f)
            }
            Text(
                text = stringResource(R.string.imu_status_chip, status.placement, status.label),
                color = color,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .background(GaugeYellow.copy(alpha = 0.1f), RoundedCornerShape(4)),
            )
        }
    }
}
