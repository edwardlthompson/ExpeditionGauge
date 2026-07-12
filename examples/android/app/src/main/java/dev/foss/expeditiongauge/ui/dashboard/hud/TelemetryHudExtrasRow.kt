package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.LocalTextScale

@Composable
fun TelemetryHudAttitudeRow(
    pitchDeg: Float,
    rollDeg: Float,
    showDriftAngle: Boolean,
    driftAngleDeg: Float?,
    modifier: Modifier = Modifier,
) {
    val scale = LocalTextScale.current
    val style = MaterialTheme.typography.titleSmall.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = MaterialTheme.typography.titleSmall.fontSize * scale * 1.1f,
    )
    val pitch = GaugeLogic.formatWholeDegrees(pitchDeg)
    val roll = GaugeLogic.formatWholeDegrees(rollDeg)
    val drift = driftAngleDeg?.let { stringResource(R.string.gauge_drift, it) }
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            text = stringResource(R.string.gauge_hud_pitch_short, pitch),
            color = GaugeScaleWhite,
            style = style,
        )
        Text(
            text = stringResource(R.string.gauge_hud_roll_short, roll),
            color = GaugeScaleWhite,
            style = style,
        )
        if (showDriftAngle && drift != null) {
            Text(text = drift, color = GaugeYellow, style = style)
        }
    }
}

@Composable
fun TelemetryHudVehicleRow(
    rpm: Float?,
    batteryVoltage: Float?,
    slipRatio: Float?,
    modifier: Modifier = Modifier,
) {
    val style = hudCubeTextStyle()
    val parts = buildList {
        rpm?.let { add(stringResource(R.string.playback_rpm, it)) }
        batteryVoltage?.let { add(stringResource(R.string.gauge_voltage_value, it)) }
        slipRatio?.let { add(stringResource(R.string.gauge_slip_ratio, it)) }
    }
    if (parts.isEmpty()) return
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        parts.forEach { line ->
            Text(text = line, color = GaugeScaleWhite, style = style)
        }
    }
}
