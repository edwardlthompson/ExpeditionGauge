package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.TpmsPressureBands
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TirePressureReading
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow

@Composable
fun TirePressurePanel(
    frontLeft: TirePressureReading,
    frontRight: TirePressureReading,
    rearLeft: TirePressureReading,
    rearRight: TirePressureReading,
    modifier: Modifier = Modifier,
    pressureUnit: PressureUnit = PressureUnit.PSI,
    tempUnit: TempUnit = TempUnit.CELSIUS,
    motionReduced: Boolean = false,
    highContrast: Boolean = false,
    @Suppress("UNUSED_PARAMETER") compact: Boolean = false,
) {
    val readings = listOf(frontLeft, frontRight, rearLeft, rearRight)
    val worstBand = TpmsPressureBands.worst(readings.map { TpmsPressureBands.band(it.psi) })
    val inset = 4.dp
    val iconSize = 44.dp

    Box(modifier = modifier.fillMaxSize().padding(inset)) {
        TireCornerCell(
            modifier = Modifier.align(Alignment.TopStart),
            labelRes = R.string.gauge_tire_fl,
            reading = frontLeft,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            horizontalAlignment = Alignment.Start,
            iconSize = iconSize,
            iconFirst = true,
        )
        TireCornerCell(
            modifier = Modifier.align(Alignment.TopEnd),
            labelRes = R.string.gauge_tire_fr,
            reading = frontRight,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            horizontalAlignment = Alignment.End,
            iconSize = iconSize,
            iconFirst = true,
        )
        TireCornerCell(
            modifier = Modifier.align(Alignment.BottomStart),
            labelRes = R.string.gauge_tire_rl,
            reading = rearLeft,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            horizontalAlignment = Alignment.Start,
            iconSize = iconSize,
            iconFirst = false,
        )
        TireCornerCell(
            modifier = Modifier.align(Alignment.BottomEnd),
            labelRes = R.string.gauge_tire_rr,
            reading = rearRight,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            horizontalAlignment = Alignment.End,
            iconSize = iconSize,
            iconFirst = false,
        )
        if (worstBand == TpmsPressureBands.Band.LOW || worstBand == TpmsPressureBands.Band.CRITICAL) {
            TpmsCenterAlert(
                band = worstBand,
                motionReduced = motionReduced,
                highContrast = highContrast,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun TireCornerCell(
    labelRes: Int,
    reading: TirePressureReading,
    pressureUnit: PressureUnit,
    tempUnit: TempUnit,
    horizontalAlignment: Alignment.Horizontal,
    iconSize: androidx.compose.ui.unit.Dp,
    iconFirst: Boolean,
    modifier: Modifier = Modifier,
) {
    val band = TpmsPressureBands.band(reading.psi)
    val valueColor = when {
        reading.stale -> GaugeYellow.copy(alpha = 0.35f)
        band == TpmsPressureBands.Band.CRITICAL -> GaugeRed
        band == TpmsPressureBands.Band.LOW -> GaugeYellow
        else -> GaugeScaleWhite
    }
    val textAlign = if (horizontalAlignment == Alignment.End) TextAlign.End else TextAlign.Start
    val pressureText = formatPressure(reading, pressureUnit)
    val tempText = formatTemp(reading.tempC, tempUnit)
    val labelText = stringResource(labelRes)

    Column(
        modifier = modifier.padding(2.dp),
        horizontalAlignment = horizontalAlignment,
    ) {
        if (iconFirst) {
            Icon(
                painter = painterResource(R.drawable.ic_tire_topdown),
                contentDescription = null,
                tint = GaugeYellow,
                modifier = Modifier.size(iconSize),
            )
            Text(text = labelText, color = GaugeYellow, style = MaterialTheme.typography.labelSmall, textAlign = textAlign)
            Text(text = pressureText, color = valueColor, style = MaterialTheme.typography.headlineSmall, textAlign = textAlign)
            Text(text = tempText, color = valueColor, style = MaterialTheme.typography.bodySmall, textAlign = textAlign)
        } else {
            Text(text = pressureText, color = valueColor, style = MaterialTheme.typography.headlineSmall, textAlign = textAlign)
            Text(text = labelText, color = GaugeYellow, style = MaterialTheme.typography.labelSmall, textAlign = textAlign)
            Text(text = tempText, color = valueColor, style = MaterialTheme.typography.bodySmall, textAlign = textAlign)
            Icon(
                painter = painterResource(R.drawable.ic_tire_topdown),
                contentDescription = null,
                tint = GaugeYellow,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}

@Composable
private fun TpmsCenterAlert(
    band: TpmsPressureBands.Band,
    motionReduced: Boolean,
    highContrast: Boolean,
    modifier: Modifier = Modifier,
) {
    val baseColor = if (band == TpmsPressureBands.Band.CRITICAL) GaugeRed else GaugeYellow
    val alpha = if (motionReduced || highContrast) {
        1f
    } else {
        val transition = rememberInfiniteTransition(label = "tpms_blink")
        val blink by transition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label = "tpms_alpha",
        )
        blink
    }
    Icon(
        painter = painterResource(R.drawable.ic_tpms_low_pressure),
        contentDescription = stringResource(R.string.gauge_tpms_low_pressure),
        tint = baseColor,
        modifier = modifier.size(40.dp).alpha(alpha),
    )
}

internal fun formatPressure(reading: TirePressureReading, unit: PressureUnit): String {
    val psi = reading.psi ?: return "--"
    val kpa = psi * 6.894757f
    val display = UnitDisplay.pressureKpaToDisplay(kpa, unit)
    val label = UnitDisplay.pressureUnitLabel(unit)
    return if (unit == PressureUnit.KPA) {
        String.format("%.0f %s", display, label)
    } else {
        String.format("%.1f %s", display, label)
    }
}

internal fun formatTemp(tempC: Float?, unit: TempUnit): String {
    if (tempC == null) return "--"
    val display = UnitDisplay.tempCToDisplay(tempC, unit)
    return String.format("%.0f%s", display, UnitDisplay.tempUnitLabel(unit))
}
