package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import dev.foss.expeditiongauge.ui.theme.LocalTextScale
import dev.foss.expeditiongauge.ui.theme.ThemeMode

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
    themeMode: ThemeMode = ThemeMode.System,
    @Suppress("UNUSED_PARAMETER") compact: Boolean = false,
) {
    val readings = listOf(frontLeft, frontRight, rearLeft, rearRight)
    val worstBand = TpmsPressureBands.worst(readings.map { TpmsPressureBands.band(it.psi) })
    val scale = LocalTextScale.current
    val pressureStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.titleLarge.fontSize * scale,
    )
    val tempStyle = MaterialTheme.typography.bodySmall.copy(
        fontWeight = FontWeight.Medium,
        fontSize = MaterialTheme.typography.bodySmall.fontSize * scale,
    )

    Box(modifier = modifier.fillMaxSize().padding(3.dp)) {
        VehicleTopDownDiagram(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(0.58f),
            highContrast = highContrast,
        )
        TpmsCornerBlock(
            modifier = Modifier.align(Alignment.TopStart).padding(top = 2.dp),
            labelRes = R.string.gauge_tire_fl_long,
            reading = frontLeft,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            alignEnd = false,
            highContrast = highContrast,
            themeMode = themeMode,
            pressureStyle = pressureStyle,
            tempStyle = tempStyle,
        )
        TpmsCornerBlock(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 2.dp),
            labelRes = R.string.gauge_tire_fr_long,
            reading = frontRight,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            alignEnd = true,
            highContrast = highContrast,
            themeMode = themeMode,
            pressureStyle = pressureStyle,
            tempStyle = tempStyle,
        )
        TpmsCornerBlock(
            modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 2.dp),
            labelRes = R.string.gauge_tire_rl_long,
            reading = rearLeft,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            alignEnd = false,
            highContrast = highContrast,
            themeMode = themeMode,
            pressureStyle = pressureStyle,
            tempStyle = tempStyle,
        )
        TpmsCornerBlock(
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 2.dp),
            labelRes = R.string.gauge_tire_rr_long,
            reading = rearRight,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            alignEnd = true,
            highContrast = highContrast,
            themeMode = themeMode,
            pressureStyle = pressureStyle,
            tempStyle = tempStyle,
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
private fun TpmsCornerBlock(
    labelRes: Int,
    reading: TirePressureReading,
    pressureUnit: PressureUnit,
    tempUnit: TempUnit,
    alignEnd: Boolean,
    highContrast: Boolean,
    themeMode: ThemeMode,
    pressureStyle: androidx.compose.ui.text.TextStyle,
    tempStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier,
) {
    val band = TpmsPressureBands.band(reading.psi)
    val valueColor = tpmsValueColor(reading, band, highContrast)
    val horizontal = if (alignEnd) Alignment.End else Alignment.Start
    val textAlign = if (alignEnd) TextAlign.End else TextAlign.Start
    val chipColors = tpmsChipColors(themeMode)

    Column(
        modifier = modifier.padding(horizontal = 1.dp),
        horizontalAlignment = horizontal,
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, chipColors.border, RoundedCornerShape(6.dp))
                .background(chipColors.background, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = stringResource(labelRes),
                color = chipColors.foreground,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                textAlign = textAlign,
            )
            Text(text = "›", color = chipColors.foreground, style = MaterialTheme.typography.labelSmall)
        }
        Text(
            text = formatPressure(reading, pressureUnit),
            color = valueColor,
            style = pressureStyle,
            textAlign = textAlign,
        )
        Text(
            text = formatTemp(reading.tempC, tempUnit),
            color = valueColor,
            style = tempStyle,
            textAlign = textAlign,
        )
        TpmsBatteryIcon(
            batteryPct = reading.batteryPct,
            highContrast = highContrast,
            modifier = Modifier.align(if (alignEnd) Alignment.End else Alignment.Start),
        )
    }
}

private fun tpmsValueColor(
    reading: TirePressureReading,
    band: TpmsPressureBands.Band,
    highContrast: Boolean,
): Color = when {
    reading.stale -> GaugeYellow.copy(alpha = if (highContrast) 0.55f else 0.4f)
    band == TpmsPressureBands.Band.CRITICAL -> GaugeRed
    band == TpmsPressureBands.Band.LOW -> GaugeYellow
    else -> GaugeScaleWhite
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
        modifier = modifier.size(36.dp).alpha(alpha),
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
        String.format("%.0f %s", display, label.replaceFirstChar { it.titlecase() })
    }
}

internal fun formatTemp(tempC: Float?, unit: TempUnit): String {
    if (tempC == null) return "--"
    val display = UnitDisplay.tempCToDisplay(tempC, unit)
    return String.format("%.0f%s", display, UnitDisplay.tempUnitLabel(unit))
}
