package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
) {
    Column(
        modifier = modifier
            .padding(SpacingSm)
            .semantics {
                contentDescription = "Speed ${GaugeLogic.formatSpeedMps(speedMps, useMetric)} ${GaugeLogic.speedUnitLabel(useMetric)}"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
