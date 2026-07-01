package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    showTime: Boolean = true,
    showAltitude: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val pad = if (compact) 2.dp else SpacingSm
    val coordStyle = if (compact) {
        MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
        )
    } else {
        MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
    }
    Column(modifier = modifier.padding(pad)) {
        if (showTime) {
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
        }
        if (latitude != null && longitude != null) {
            Text(
                text = stringResource(R.string.gauge_coords_line1, formatDms(latitude, true)),
                color = GaugeScaleWhite,
                style = coordStyle,
            )
            Text(
                text = stringResource(R.string.gauge_coords_line2, formatDms(longitude, false)),
                color = GaugeScaleWhite,
                style = coordStyle,
            )
        } else {
            Text(
                text = stringResource(R.string.gauge_gps_no_fix),
                color = GaugeYellow,
                style = coordStyle,
            )
        }
        if (showAltitude) {
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
                        style = coordStyle,
                    )
                }
            }
        }
        if (showDriftAngle && driftAngleDeg != null) {
            Text(
                text = stringResource(R.string.gauge_drift, driftAngleDeg),
                color = GaugeYellow,
                style = coordStyle,
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
