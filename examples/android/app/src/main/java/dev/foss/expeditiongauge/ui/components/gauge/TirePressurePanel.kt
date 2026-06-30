package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TirePressureReading
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm

@Composable
fun TirePressurePanel(
    frontLeft: TirePressureReading,
    frontRight: TirePressureReading,
    rearLeft: TirePressureReading,
    rearRight: TirePressureReading,
    modifier: Modifier = Modifier,
    pressureUnit: PressureUnit = PressureUnit.PSI,
    tempUnit: TempUnit = TempUnit.CELSIUS,
) {
    Column(
        modifier = modifier.padding(SpacingSm),
        verticalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TireCorner(R.string.gauge_tire_fl, frontLeft, pressureUnit, tempUnit)
            TireCorner(R.string.gauge_tire_fr, frontRight, pressureUnit, tempUnit)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TireCorner(R.string.gauge_tire_rl, rearLeft, pressureUnit, tempUnit)
            TireCorner(R.string.gauge_tire_rr, rearRight, pressureUnit, tempUnit)
        }
    }
}

@Composable
private fun TireCorner(
    labelRes: Int,
    reading: TirePressureReading,
    pressureUnit: PressureUnit,
    tempUnit: TempUnit,
) {
    val color = if (reading.stale) GaugeYellow.copy(alpha = 0.35f) else GaugeScaleWhite
    Column {
        Text(text = stringResource(labelRes), color = GaugeYellow)
        Text(
            text = formatPressure(reading, pressureUnit),
            color = color,
            style = MaterialTheme.typography.headlineMedium,
        )
        reading.tempC?.let { tempC ->
            Text(
                text = formatTemp(tempC, tempUnit),
                color = color,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private fun formatPressure(reading: TirePressureReading, unit: PressureUnit): String {
    val psi = reading.psi ?: return "--"
    return when (unit) {
        PressureUnit.PSI -> String.format("%.1f psi", psi)
        PressureUnit.KPA -> String.format("%.0f kPa", psi * 6.894757f)
    }
}

private fun formatTemp(tempC: Float, unit: TempUnit): String = when (unit) {
    TempUnit.CELSIUS -> String.format("%.0f°C", tempC)
    TempUnit.FAHRENHEIT -> String.format("%.0f°F", tempC * 9f / 5f + 32f)
}
