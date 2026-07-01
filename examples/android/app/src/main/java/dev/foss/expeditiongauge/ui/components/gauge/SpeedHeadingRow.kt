package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.ui.theme.GaugeLabelTextStyle
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
    modifier: Modifier = Modifier,
) {
    if (!showSpeed && !showHeading) return
    val scale = LocalTextScale.current
    val digitStyle = androidx.compose.ui.text.TextStyle(
        fontSize = (28f * scale).sp,
        lineHeight = (32f * scale).sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top,
    ) {
        if (showSpeed) {
            HudCompactDigitColumn(
                value = GaugeLogic.formatSpeedPadded(speedMps, useMetric),
                unit = GaugeLogic.speedUnitLabel(useMetric),
                digitStyle = digitStyle,
                modifier = Modifier.widthIn(min = 88.dp),
            )
        }
        if (showHeading) {
            HudCompactDigitColumn(
                value = GaugeLogic.formatHeadingPadded(headingDeg),
                unit = stringResource(R.string.gauge_hdg_label),
                digitStyle = digitStyle,
                modifier = Modifier.widthIn(min = 72.dp),
            )
        }
    }
}

@Composable
private fun HudCompactDigitColumn(
    value: String,
    unit: String,
    digitStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = value, color = GaugeScaleWhite, style = digitStyle)
        Text(
            text = unit,
            color = GaugeYellow,
            style = GaugeLabelTextStyle.copy(fontSize = (11f * LocalTextScale.current).sp),
        )
    }
}
