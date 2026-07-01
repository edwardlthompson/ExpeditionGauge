package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.GaugeLogic
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
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(SpacingSm)) {
        val timeLabel = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        Text(text = stringResource(R.string.gauge_time, timeLabel), color = GaugeYellow)
        latitude?.let { lat ->
            longitude?.let { lon ->
                Text(
                    text = stringResource(R.string.gauge_coords_line1, formatDms(lat, true)),
                    color = GaugeScaleWhite,
                )
                Text(
                    text = stringResource(R.string.gauge_coords_line2, formatDms(lon, false)),
                    color = GaugeScaleWhite,
                )
            }
        }
        altitudeM?.let {
            Text(
                text = stringResource(R.string.gauge_altitude, GaugeLogic.formatAltitude(it, useMetric)),
                color = GaugeScaleWhite,
            )
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
