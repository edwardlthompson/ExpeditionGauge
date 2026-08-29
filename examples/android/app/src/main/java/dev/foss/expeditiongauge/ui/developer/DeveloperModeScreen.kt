package dev.foss.expeditiongauge.ui.developer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.ui.navigation.GaugeBackHandler
import dev.foss.expeditiongauge.ui.pidsniffer.PidSnifferPanel
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun DeveloperModeScreen(
    telemetry: TelemetrySnapshot,
    madgwickBeta: Float,
    onMadgwickBetaChange: (Float) -> Unit,
    onBack: () -> Unit,
    speedUnit: SpeedUnit = SpeedUnit.METRIC,
    lastSniff: String? = null,
    onPidSniff: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    GaugeBackHandler(onBack = onBack)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .verticalScroll(rememberScrollState())
            .testTag("developer_mode_screen"),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.developer_mode_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        Text(text = stringResource(R.string.developer_mode_intro), color = GaugeScaleWhite)
        val speedDisplay = UnitDisplay.speedMpsToDisplay(telemetry.speedMps, speedUnit)
        val speedLabel = UnitDisplay.speedUnitLabel(speedUnit)
        Text(
            text = stringResource(
                R.string.developer_raw_sensors,
                telemetry.pitchDeg,
                telemetry.rollDeg,
                telemetry.latG,
                telemetry.lonG,
                telemetry.headingDeg,
                speedDisplay,
                speedLabel,
            ),
            color = GaugeScaleWhite,
            modifier = Modifier.testTag("developer_raw_readout"),
        )
        Text(text = stringResource(R.string.developer_madgwick_beta, madgwickBeta), color = GaugeScaleWhite)
        Slider(
            value = madgwickBeta,
            onValueChange = onMadgwickBetaChange,
            valueRange = 0.01f..0.5f,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("developer_madgwick_slider"),
        )
        PidSnifferPanel(last = lastSniff, onSend = onPidSniff)
        Button(onClick = onBack, modifier = Modifier.testTag("developer_mode_close")) {
            Text(stringResource(R.string.settings_close))
        }
    }
}
