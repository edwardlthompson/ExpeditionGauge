package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun GpsStatusChip(
    gpsFix: Boolean,
    gpsSource: String,
    numSatellites: Int?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(SpacingSm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.SatelliteAlt,
            contentDescription = stringResource(
                if (gpsFix) R.string.gauge_gps_fix else R.string.gauge_gps_no_fix,
            ),
            tint = if (gpsFix) GaugeYellow else GaugeYellow.copy(alpha = 0.4f),
        )
        Text(
            text = stringResource(
                R.string.gps_status_chip,
                gpsSource.uppercase(),
                numSatellites ?: 0,
            ),
            color = GaugeYellow,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = SpacingSm),
        )
    }
}
