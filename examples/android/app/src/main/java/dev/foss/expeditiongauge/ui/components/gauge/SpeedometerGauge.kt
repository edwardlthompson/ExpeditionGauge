package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.foss.expeditiongauge.ui.theme.LocalTextScale
import dev.foss.expeditiongauge.ui.theme.scaledGaugeSpeedTextStyle
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun SpeedometerGauge(
    speedMps: Float,
    modifier: Modifier = Modifier,
    useMetric: Boolean = true,
    speedFromObd: Boolean = false,
    gaugeSizeDp: Dp = 160.dp,
) {
    Column(
        modifier = modifier
            .padding(SpacingSm)
            .semantics {
                contentDescription = "Speed ${GaugeLogic.formatSpeedMps(speedMps, useMetric)} ${GaugeLogic.speedUnitLabel(useMetric)}"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(modifier = Modifier.size(gaugeSizeDp)) {
            val stroke = 3f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = GaugeScaleWhite,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(stroke / 2f, stroke / 2f),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Butt),
            )
        }
        Text(
            text = GaugeLogic.formatSpeedMps(speedMps, useMetric),
            color = GaugeScaleWhite,
            style = scaledGaugeSpeedTextStyle(LocalTextScale.current),
        )
        Text(
            text = stringResource(R.string.gauge_speed_unit, GaugeLogic.speedUnitLabel(useMetric)),
            color = GaugeYellow,
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
        )
        if (speedFromObd) {
            Text(
                text = stringResource(R.string.gauge_speed_obd),
                color = GaugeYellow,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            )
        }
    }
}
