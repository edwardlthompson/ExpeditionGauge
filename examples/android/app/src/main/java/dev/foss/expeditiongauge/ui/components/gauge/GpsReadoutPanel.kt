package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GpsReadoutPanel(
    latitude: Double?,
    longitude: Double?,
    altitudeM: Double?,
    driftAngleDeg: Float?,
    showDriftAngle: Boolean,
    useMetric: Boolean = true,
    compact: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val pad = if (compact) SpacingSm / 2 else SpacingSm
    androidx.compose.foundation.layout.Column(modifier = modifier.padding(pad)) {
        val timeLabel = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_clock),
                contentDescription = null,
                tint = GaugeYellow,
                modifier = Modifier.size(14.dp).padding(end = 4.dp),
            )
            Text(text = stringResource(R.string.gauge_time, timeLabel), color = GaugeYellow)
        }
        latitude?.let { lat ->
            longitude?.let { lon ->
                Text(
                    text = stringResource(R.string.gauge_coords_line1, formatDms(lat, true)),
                    color = GaugeScaleWhite,
                    style = if (compact) {
                        androidx.compose.material3.MaterialTheme.typography.labelSmall
                    } else {
                        androidx.compose.material3.MaterialTheme.typography.bodyMedium
                    },
                )
                Text(
                    text = stringResource(R.string.gauge_coords_line2, formatDms(lon, false)),
                    color = GaugeScaleWhite,
                    style = if (compact) {
                        androidx.compose.material3.MaterialTheme.typography.labelSmall
                    } else {
                        androidx.compose.material3.MaterialTheme.typography.bodyMedium
                    },
                )
            }
        }
        altitudeM?.let {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_elevation),
                    contentDescription = null,
                    tint = GaugeYellow,
                    modifier = Modifier.size(14.dp).padding(end = 4.dp),
                )
                Text(
                    text = stringResource(
                        R.string.gauge_altitude,
                        "${UnitDisplay.altitudeMToDisplay(it, useMetric)} ${UnitDisplay.altitudeUnitLabel(useMetric)}",
                    ),
                    color = GaugeScaleWhite,
                )
            }
        }
        if (showDriftAngle && driftAngleDeg != null) {
            Text(
                text = stringResource(R.string.gauge_drift, driftAngleDeg),
                color = GaugeYellow,
            )
        }
    }
}

private fun formatDms(value: Double, isLatitude: Boolean): String {
    val abs = kotlin.math.abs(value)
    val degrees = abs.toInt()
    val minutes = ((abs - degrees) * 60).toInt()
    val seconds = ((abs - degrees - minutes / 60.0) * 3600)
    val hemisphere = when {
        isLatitude && value >= 0 -> "N"
        isLatitude -> "S"
        value >= 0 -> "E"
        else -> "W"
    }
    return "$degrees°$minutes'${"%.1f".format(seconds)}\"$hemisphere"
}
