package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.car.gauge.SatCountBadge
import dev.foss.expeditiongauge.telemetry.SensorLinkState
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

@Composable
fun TelemetryHudLinkRow(
    links: SensorLinkState,
    modifier: Modifier = Modifier,
    fillRow: Boolean = false,
    satelliteCount: Int? = null,
) {
    BoxWithConstraints(modifier.then(if (fillRow) Modifier.fillMaxSize() else Modifier.fillMaxWidth())) {
        val iconDp = if (fillRow) maxHeight * 0.72f else hudCubeIconDp()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (fillRow) Modifier.fillMaxSize() else Modifier)
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
        LinkIcon(
            linked = links.gpsLinked,
            imageVector = Icons.Filled.SatelliteAlt,
            contentDescription = buildString {
                append(
                    stringResource(
                        when {
                            links.gpsLinked && links.gpsSource == "external" ->
                                R.string.link_gps_external_connected
                            links.gpsLinked -> R.string.link_gps_connected
                            else -> R.string.link_gps_disconnected
                        },
                    ),
                )
                if (satelliteCount != null) {
                    append(". ")
                    append(stringResource(R.string.gps_status_chip_hud, satelliteCount))
                }
            },
            iconDp = iconDp,
            satelliteCount = satelliteCount,
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
}

@Composable
private fun LinkIcon(
    linked: Boolean,
    contentDescription: String,
    iconDp: Dp,
    imageVector: ImageVector? = null,
    painterRes: Int? = null,
    satelliteCount: Int? = null,
) {
    val tint = if (linked) GaugeYellow else GaugeYellow.copy(alpha = 0.35f)
    Box(Modifier.size(iconDp), contentAlignment = Alignment.Center) {
        when {
            imageVector != null -> Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.fillMaxSize(),
            )
            painterRes != null -> Icon(
                painter = painterResource(painterRes),
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (satelliteCount != null) {
            val bubble = GaugeRed.toArgb()
            Canvas(Modifier.fillMaxSize()) {
                SatCountBadge.draw(
                    drawContext.canvas.nativeCanvas,
                    size.width / 2f,
                    size.height / 2f,
                    size.minDimension,
                    satelliteCount,
                    bubble,
                )
            }
        }
    }
}
