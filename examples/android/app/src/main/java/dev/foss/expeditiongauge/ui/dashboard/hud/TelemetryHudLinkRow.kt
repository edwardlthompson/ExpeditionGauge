package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.telemetry.SensorLinkState
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

@Composable
fun TelemetryHudLinkRow(
    links: SensorLinkState,
    modifier: Modifier = Modifier,
) {
    val iconDp = hudCubeIconDp()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LinkIcon(
            linked = links.gpsLinked,
            imageVector = Icons.Filled.SatelliteAlt,
            contentDescription = stringResource(
                when {
                    links.gpsLinked && links.gpsSource == "external" ->
                        R.string.link_gps_external_connected
                    links.gpsLinked -> R.string.link_gps_connected
                    else -> R.string.link_gps_disconnected
                },
            ),
            iconDp = iconDp,
        )
        LinkIcon(
            linked = links.obdLinked,
            imageVector = Icons.Filled.Speed,
            contentDescription = stringResource(
                if (links.obdLinked) R.string.link_obd_connected else R.string.link_obd_disconnected,
            ),
            iconDp = iconDp,
        )
        LinkIcon(
            linked = links.tpmsLinked,
            painterRes = R.drawable.ic_wheel,
            contentDescription = stringResource(
                if (links.tpmsLinked) R.string.link_tpms_connected else R.string.link_tpms_disconnected,
            ),
            iconDp = iconDp,
        )
        LinkIcon(
            linked = links.imuLinked,
            imageVector = Icons.Filled.Sensors,
            contentDescription = stringResource(
                if (links.imuLinked) R.string.link_imu_connected else R.string.link_imu_disconnected,
            ),
            iconDp = iconDp,
        )
    }
}

@Composable
private fun LinkIcon(
    linked: Boolean,
    contentDescription: String,
    iconDp: Dp,
    imageVector: ImageVector? = null,
    painterRes: Int? = null,
) {
    val tint = if (linked) GaugeYellow else GaugeYellow.copy(alpha = 0.35f)
    when {
        imageVector != null -> Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconDp),
        )
        painterRes != null -> Icon(
            painter = painterResource(painterRes),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconDp),
        )
    }
}
