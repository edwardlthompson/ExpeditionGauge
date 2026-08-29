package dev.foss.expeditiongauge.ui.phonehuddtc

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gearestimate.GearEstimate
import dev.foss.expeditiongauge.multiecu.MultiEcuHeaders
import dev.foss.expeditiongauge.obdtemps.ObdTempsVoltage
import dev.foss.expeditiongauge.ui.theme.LocalColorblindHud

@Composable
fun PhoneHudStatusLines(lines: List<String>, modifier: Modifier = Modifier) {
    if (lines.isEmpty()) return
    Column(modifier = modifier) {
        lines.forEach { PhoneHudStatusLine(it) }
    }
}

@Composable
fun PhoneHudStatusLine(line: String, modifier: Modifier = Modifier) {
    val spokenRes = when {
        line.startsWith("I/M") -> R.string.im_readiness_cd
        line.startsWith("VIN") -> R.string.vin_last6_cd
        line.startsWith("TFT") || line.startsWith("EGT") -> R.string.ford_mode22_temps_cd
        line.startsWith("MAP") || line.startsWith("AFR") || line.startsWith("Boost") ->
            R.string.boost_pids_cd
        ObdTempsVoltage.matches(line) -> R.string.obd_temps_voltage_cd
        GearEstimate.matches(line) -> R.string.gear_estimate_cd
        MultiEcuHeaders.matches(line) -> R.string.multi_ecu_cd
        else -> R.string.obd_trip_cd
    }
    val spoken = stringResource(spokenRes, line)
    Text(
        text = line,
        color = if (line.contains("not ready")) {
            LocalColorblindHud.current.alertYellow
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .testTag(
                when {
                    line.startsWith("I/M") -> "im_readiness"
                    line.startsWith("VIN") -> "vin_last6"
                    line.startsWith("TFT") || line.startsWith("EGT") -> "ford_mode22_temps"
                    line.startsWith("MAP") || line.startsWith("AFR") || line.startsWith("Boost") ->
                        "optional_boost_pids"
                    ObdTempsVoltage.matches(line) -> "obd_temps_voltage"
                    GearEstimate.matches(line) -> "gear_estimate"
                    MultiEcuHeaders.matches(line) -> "multi_ecu_headers"
                    else -> "obd_trip_since_clear"
                },
            )
            .semantics { contentDescription = spoken },
    )
}
