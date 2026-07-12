package dev.foss.expeditiongauge.ui.components.gauge

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.ExpeditionGaugeApplication
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.CoordinateFormat
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.ui.dashboard.hud.HudAutoFitText
import dev.foss.expeditiongauge.ui.dashboard.hud.hudCubeIconDp
import dev.foss.expeditiongauge.ui.dashboard.hud.hudCubeTextStyle
import dev.foss.expeditiongauge.ui.dashboard.hud.rememberHudClockLabel
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.LocalTextScale
import dev.foss.expeditiongauge.ui.theme.SpacingSm
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
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
    hudCube: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val scale = LocalTextScale.current
    val pad = when {
        hudCube -> 4.dp
        compact -> 2.dp
        else -> SpacingSm
    }
    val cubeStyle = if (hudCube) hudCubeTextStyle() else null
    val coordStyle = cubeStyle ?: when {
        compact -> MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
        )
        else -> MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
    }
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val copiedMsg = stringResource(R.string.coords_copied)
    val scope = rememberCoroutineScope()
    val settings = (context.applicationContext as? ExpeditionGaugeApplication)?.services?.settingsPreferences
    val decimalFlow = remember(settings) { settings?.coordFormatDecimal ?: flowOf(false) }
    val useDecimal by decimalFlow.collectAsStateWithLifecycle(initialValue = false)
    val mode = if (useDecimal) CoordinateFormat.Mode.DECIMAL else CoordinateFormat.Mode.DMS
    val baseSp = 14f * scale

    Column(
        modifier = modifier.padding(pad),
        horizontalAlignment = if (hudCube) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        if (showTime) {
            val timeLabel = rememberHudClockLabel()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint = GaugeYellow,
                    modifier = Modifier.size((14f * scale).dp).padding(end = 4.dp),
                )
                Text(text = stringResource(R.string.gauge_time, timeLabel), color = GaugeYellow)
            }
        }
        if (hudCube) {
            HudCubeCoordsBlock(
                latitude = latitude,
                longitude = longitude,
                mode = mode,
                style = coordStyle,
                baseSp = baseSp,
                onToggleFormat = {
                    settings?.let { prefs ->
                        scope.launch { prefs.setCoordFormatDecimal(!useDecimal) }
                    }
                },
                onCopy = { pair ->
                    clipboard.setText(AnnotatedString(pair))
                    Toast.makeText(context, copiedMsg, Toast.LENGTH_SHORT).show()
                },
            )
        } else if (latitude != null && longitude != null) {
            Text(
                text = stringResource(
                    R.string.gauge_coords_line1,
                    CoordinateFormat.formatLine(latitude, true, mode),
                ),
                color = GaugeScaleWhite,
                style = coordStyle,
            )
            Text(
                text = stringResource(
                    R.string.gauge_coords_line2,
                    CoordinateFormat.formatLine(longitude, false, mode),
                ),
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
        if (showAltitude && !hudCube) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HudCubeCoordsBlock(
    latitude: Double?,
    longitude: Double?,
    mode: CoordinateFormat.Mode,
    style: androidx.compose.ui.text.TextStyle,
    baseSp: Float,
    onToggleFormat: () -> Unit,
    onCopy: (String) -> Unit,
) {
    val hasFix = latitude != null && longitude != null
    val iconDp = hudCubeIconDp()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (hasFix) {
                    Modifier.combinedClickable(
                        onClick = onToggleFormat,
                        onLongClick = {
                            onCopy(CoordinateFormat.formatPair(latitude!!, longitude!!, mode))
                        },
                    )
                } else {
                    Modifier
                },
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (hasFix) {
            CoordIconLine(
                iconRes = R.drawable.ic_lat_ns,
                text = CoordinateFormat.formatLine(latitude!!, true, mode),
                style = style,
                baseSp = baseSp,
                iconDp = iconDp,
            )
            CoordIconLine(
                iconRes = R.drawable.ic_lon_ew,
                text = CoordinateFormat.formatLine(longitude!!, false, mode),
                style = style,
                baseSp = baseSp,
                iconDp = iconDp,
            )
        } else {
            HudAutoFitText(
                text = stringResource(R.string.gauge_gps_no_fix),
                color = GaugeYellow,
                style = style,
                minSp = 9f,
                maxSp = baseSp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CoordIconLine(
    iconRes: Int,
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    baseSp: Float,
    iconDp: androidx.compose.ui.unit.Dp,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = GaugeYellow,
            modifier = Modifier.size(iconDp).padding(end = 4.dp),
        )
        HudAutoFitText(
            text = text,
            color = GaugeScaleWhite,
            style = style,
            minSp = 9f,
            maxSp = baseSp,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}
