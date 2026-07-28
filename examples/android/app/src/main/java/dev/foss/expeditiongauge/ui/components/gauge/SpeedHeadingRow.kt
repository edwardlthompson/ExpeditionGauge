package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.ui.dashboard.hud.HudAutoFitText
import dev.foss.expeditiongauge.ui.dashboard.hud.hudCubeTextStyle
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.LocalTextScale

@Composable
fun SpeedHeadingRow(
    speedMps: Float,
    headingDeg: Float,
    useMetric: Boolean,
    showSpeed: Boolean,
    showHeading: Boolean,
    altitudeM: Double? = null,
    showAltitude: Boolean = false,
    @Suppress("UNUSED_PARAMETER") uniformCube: Boolean = false,
    speedOverLimit: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (!showSpeed && !showHeading && !showAltitude) return
    val style = hudCubeTextStyle()
    val baseSp = 14f * LocalTextScale.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showSpeed) {
            HudCompactDigitColumn(
                value = GaugeLogic.formatSpeedPadded(speedMps, useMetric),
                unit = GaugeLogic.speedUnitLabel(useMetric),
                style = style,
                baseSp = baseSp,
                contentAlignment = Alignment.Start,
                valueColor = if (speedOverLimit) GaugeRed else GaugeScaleWhite,
                valueBold = speedOverLimit,
                modifier = Modifier.weight(1f),
            )
        }
        if (showHeading) {
            HudCompactDigitColumn(
                value = GaugeLogic.formatHeadingPadded(headingDeg),
                unit = stringResource(R.string.gauge_hdg_label),
                style = style,
                baseSp = baseSp,
                contentAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            )
        }
        if (showAltitude) {
            val elevUnit = if (useMetric) "M" else "FT"
            HudCompactDigitColumn(
                value = GaugeLogic.formatAltitudePadded(altitudeM, useMetric),
                unit = stringResource(R.string.gauge_elev_label_unit, elevUnit),
                style = style,
                baseSp = baseSp,
                contentAlignment = Alignment.End,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HudCompactDigitColumn(
    value: String,
    unit: String,
    style: androidx.compose.ui.text.TextStyle,
    baseSp: Float,
    contentAlignment: Alignment.Horizontal,
    modifier: Modifier = Modifier,
    valueColor: Color = GaugeScaleWhite,
    valueBold: Boolean = false,
) {
    val valueStyle = if (valueBold) style.copy(fontWeight = FontWeight.Bold) else style
    Column(
        modifier = modifier,
        horizontalAlignment = contentAlignment,
    ) {
        HudAutoFitText(
            text = value,
            color = valueColor,
            style = valueStyle,
            minSp = 9f,
            maxSp = baseSp,
            modifier = Modifier.fillMaxWidth(),
        )
        HudAutoFitText(
            text = unit,
            color = GaugeYellow,
            style = style,
            minSp = 9f,
            maxSp = baseSp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
