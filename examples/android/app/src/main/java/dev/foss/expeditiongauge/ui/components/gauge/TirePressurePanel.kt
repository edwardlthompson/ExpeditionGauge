package dev.foss.expeditiongauge.ui.components.gauge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
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
) {
    Column(
        modifier = modifier.padding(SpacingSm),
        verticalArrangement = Arrangement.spacedBy(SpacingSm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TireCorner(labelRes = R.string.gauge_tire_fl, reading = frontLeft)
            TireCorner(labelRes = R.string.gauge_tire_fr, reading = frontRight)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TireCorner(labelRes = R.string.gauge_tire_rl, reading = rearLeft)
            TireCorner(labelRes = R.string.gauge_tire_rr, reading = rearRight)
        }
    }
}

@Composable
private fun TireCorner(labelRes: Int, reading: TirePressureReading) {
    Column {
        Text(text = stringResource(labelRes), color = GaugeYellow)
        Text(
            text = reading.psi?.let { stringResource(R.string.gauge_tire_psi, it) }
                ?: stringResource(R.string.gauge_tire_no_data),
            color = GaugeScaleWhite,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
        )
    }
}
