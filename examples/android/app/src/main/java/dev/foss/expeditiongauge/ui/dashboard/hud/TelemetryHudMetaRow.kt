package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.LocalTextScale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelemetryHudMetaRow(
    altitudeM: Double?,
    useMetric: Boolean,
    gpsFix: Boolean,
    gpsSource: String,
    numSatellites: Int?,
    hdop: Float?,
    modifier: Modifier = Modifier,
) {
    val scale = LocalTextScale.current
    val valueStyle = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.titleMedium.fontSize * scale * 1.05f,
    )
    val labelStyle = MaterialTheme.typography.labelMedium.copy(
        fontSize = (12f * scale).sp,
    )
    val timeLabel = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
    val altText = altitudeM?.let {
        "${UnitDisplay.altitudeMToDisplay(it, useMetric)} ${UnitDisplay.altitudeUnitLabel(useMetric)}"
    } ?: "--"

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_elevation),
                contentDescription = null,
                tint = GaugeYellow,
                modifier = Modifier.size(18.dp).padding(end = 4.dp),
            )
            Text(text = altText, color = GaugeScaleWhite, style = valueStyle)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_clock),
                contentDescription = null,
                tint = GaugeYellow,
                modifier = Modifier.size(18.dp).padding(end = 4.dp),
            )
            Text(text = timeLabel, color = GaugeScaleWhite, style = valueStyle)
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.SatelliteAlt,
            contentDescription = stringResource(
                if (gpsFix) R.string.gauge_gps_fix else R.string.gauge_gps_no_fix,
            ),
            tint = if (gpsFix) GaugeYellow else GaugeYellow.copy(alpha = 0.45f),
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(
                R.string.gps_status_chip,
                gpsSource.uppercase(),
                numSatellites ?: 0,
                hdop ?: 0f,
            ),
            color = GaugeYellow,
            style = labelStyle,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}
