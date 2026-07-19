package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.LocalTextScale

@Composable
fun TelemetryHudSatRow(
    gpsFix: Boolean,
    numSatellites: Int?,
    modifier: Modifier = Modifier,
) {
    val style = hudCubeTextStyle()
    val iconDp = hudCubeIconDp()
    val baseSp = 14f * LocalTextScale.current
    val satText = stringResource(
        R.string.gps_status_chip_hud,
        numSatellites ?: 0,
    )
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Icon(
            imageVector = Icons.Filled.SatelliteAlt,
            contentDescription = stringResource(
                if (gpsFix) R.string.gauge_gps_fix else R.string.gauge_gps_no_fix,
            ),
            tint = if (gpsFix) GaugeYellow else GaugeYellow.copy(alpha = 0.45f),
            modifier = Modifier.size(iconDp),
        )
        HudAutoFitText(
            text = satText,
            color = GaugeYellow,
            style = style,
            minSp = 9f,
            maxSp = baseSp,
            maxLines = 1,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

@Composable
fun TelemetryHudClockRow(modifier: Modifier = Modifier) {
    val style = hudCubeTextStyle()
    val iconDp = hudCubeIconDp()
    val baseSp = 14f * LocalTextScale.current
    val clock = rememberHudClockLabels()
    val line = stringResource(R.string.gauge_time_date_hud, clock.time, clock.date)
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = null,
            tint = GaugeYellow,
            modifier = Modifier.size(iconDp),
        )
        HudAutoFitText(
            text = line,
            color = GaugeScaleWhite,
            style = style,
            minSp = 9f,
            maxSp = baseSp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 6.dp)
                .weight(1f),
        )
    }
}
