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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.telemetry.ImuStatusEntry
import dev.foss.expeditiongauge.ui.theme.GaugeGreen
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun ImuStatusStrip(
    statuses: List<ImuStatusEntry>,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val connected = statuses.filter { it.connected }
    if (connected.isEmpty()) {
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
        connected.forEach { status ->
            val color = imuSignalColor(status.signalQuality)
            Text(
                text = stringResource(R.string.imu_status_chip, status.placement, status.label),
                color = color,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(4))
                    .padding(horizontal = SpacingSm, vertical = SpacingSm / 2),
            )
        }
    }
}

private fun imuSignalColor(quality: String): Color = when (quality) {
    "Good" -> GaugeGreen
    "Fair" -> GaugeYellow
    "Poor", "Disconnected" -> GaugeRed
    else -> GaugeYellow.copy(alpha = 0.5f)
}
