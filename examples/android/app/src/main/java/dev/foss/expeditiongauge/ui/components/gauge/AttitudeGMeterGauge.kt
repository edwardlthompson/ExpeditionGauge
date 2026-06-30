package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.AttitudeBallLogic
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.gauge.GaugeZone
import dev.foss.expeditiongauge.ui.theme.GaugeBall
import dev.foss.expeditiongauge.ui.theme.GaugeGreen
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun AttitudeGMeterGauge(
    pitchDeg: Float,
    rollDeg: Float,
    onCalibrate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ball = AttitudeBallLogic.mapPitchRoll(pitchDeg, rollDeg)
    Column(
        modifier = modifier.padding(SpacingSm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f * 0.9f
            listOf(
                GaugeLogic.RING_10_DEG,
                GaugeLogic.RING_20_DEG,
                GaugeLogic.RING_30_DEG,
            ).forEach { ringDeg ->
                val ringRadius = radius * AttitudeBallLogic.ringRadiusFraction(ringDeg)
                drawCircle(
                    color = GaugeScaleWhite,
                    radius = ringRadius,
                    center = center,
                    style = Stroke(width = 1.5f),
                )
            }
            drawLine(GaugeScaleWhite, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), 1f)
            drawLine(GaugeScaleWhite, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), 1f)
            val ballColor = when (ball.zone) {
                GaugeZone.Safe -> GaugeGreen
                GaugeZone.Caution -> GaugeYellow
                GaugeZone.Critical -> GaugeRed
            }
            val ballOffset = Offset(
                center.x + ball.normalizedX * radius,
                center.y + ball.normalizedY * radius,
            )
            drawCircle(color = ballColor, radius = 10f, center = ballOffset)
            drawCircle(color = GaugeBall, radius = 6f, center = ballOffset)
        }
        Text(
            text = stringResource(R.string.gauge_pitch, GaugeLogic.formatSignedDegrees(pitchDeg)),
            color = GaugeScaleWhite,
        )
        Text(
            text = stringResource(R.string.gauge_roll, GaugeLogic.formatSignedDegrees(rollDeg)),
            color = GaugeScaleWhite,
        )
        Button(onClick = onCalibrate) {
            Text(stringResource(R.string.gauge_calibrate))
        }
    }
}
