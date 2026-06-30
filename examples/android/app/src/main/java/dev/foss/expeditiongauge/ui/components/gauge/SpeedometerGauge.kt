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
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun SpeedometerGauge(
    speedMps: Float,
    modifier: Modifier = Modifier,
    useMetric: Boolean = true,
) {
    Column(
        modifier = modifier.padding(SpacingSm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
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
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.gauge_speed_unit, GaugeLogic.speedUnitLabel(useMetric)),
            color = GaugeYellow,
        )
    }
}
