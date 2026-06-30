package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun StatusIcons(
    gpsFix: Boolean,
    batteryVoltage: Float?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(SpacingSm),
        horizontalArrangement = Arrangement.spacedBy(SpacingSm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.SatelliteAlt,
            contentDescription = stringResource(
                if (gpsFix) R.string.gauge_gps_fix else R.string.gauge_gps_no_fix,
            ),
            tint = if (gpsFix) GaugeYellow else GaugeYellow.copy(alpha = 0.4f),
        )
        batteryVoltage?.let { voltage ->
            Icon(
                imageVector = Icons.Filled.Bolt,
                contentDescription = stringResource(R.string.gauge_voltage),
                tint = GaugeYellow,
            )
            Text(
                text = stringResource(R.string.gauge_voltage_value, voltage),
                color = GaugeYellow,
            )
        }
    }
}
