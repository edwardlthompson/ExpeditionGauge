package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.gauge.UnitDisplay
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SettingsAlertOptions(
    thresholds: AlertThresholds,
    onThresholdsChange: (AlertThresholds) -> Unit,
    speedUnit: dev.foss.expeditiongauge.settings.SpeedUnit = dev.foss.expeditiongauge.settings.SpeedUnit.METRIC,
    pressureUnit: dev.foss.expeditiongauge.settings.PressureUnit = dev.foss.expeditiongauge.settings.PressureUnit.PSI,
) {
    if (!FeatureFlags.alertsEnabled) return
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Text(text = stringResource(R.string.settings_alerts_title))
        SettingsSwitchRow(
            label = stringResource(R.string.alerts_master_toggle),
            checked = thresholds.masterEnabled,
            onCheckedChange = { onThresholdsChange(thresholds.copy(masterEnabled = it)) },
            modifier = Modifier.testTag("settings_alerts_master"),
        )
        AlertField(R.string.alerts_max_lat_g, thresholds.maxLatG, "settings_alerts_lat_g") {
            onThresholdsChange(thresholds.copy(maxLatG = it))
        }
        AlertField(R.string.alerts_max_beta, thresholds.maxAbsDriftAngleDeg, "settings_alerts_beta") {
            onThresholdsChange(thresholds.copy(maxAbsDriftAngleDeg = it))
        }
        AlertField(R.string.alerts_max_slip, thresholds.maxSlipRatio, "settings_alerts_slip") {
            onThresholdsChange(thresholds.copy(maxSlipRatio = it))
        }
        AlertField(R.string.alerts_max_pitch, thresholds.maxPitchDeg, "settings_alerts_pitch") {
            onThresholdsChange(thresholds.copy(maxPitchDeg = it))
        }
        AlertField(R.string.alerts_max_roll, thresholds.maxRollDeg, "settings_alerts_roll") {
            onThresholdsChange(thresholds.copy(maxRollDeg = it))
        }
        AlertField(R.string.alerts_max_rpm, thresholds.maxRpm, "settings_alerts_rpm") {
            onThresholdsChange(thresholds.copy(maxRpm = it))
        }
        AlertField(
            label = stringResource(R.string.alerts_max_speed, UnitDisplay.speedAlertLabel(speedUnit)),
            value = thresholds.maxSpeedMps?.let { UnitDisplay.speedMpsToDisplay(it, speedUnit) },
            testTag = "settings_alerts_speed",
        ) {
            val factor = UnitDisplay.speedMpsToDisplay(1f, speedUnit)
            onThresholdsChange(thresholds.copy(maxSpeedMps = it?.div(factor)))
        }
        AlertField(R.string.alerts_min_fuel_kmpl, thresholds.minFuelEconomyKmpl, "settings_alerts_fuel") {
            onThresholdsChange(thresholds.copy(minFuelEconomyKmpl = it))
        }
        AlertField(
            label = stringResource(R.string.alerts_min_pressure_kpa, UnitDisplay.pressureUnitLabel(pressureUnit)),
            value = thresholds.minTirePressureKpa?.let { UnitDisplay.pressureKpaToDisplay(it, pressureUnit) },
            testTag = "settings_alerts_pressure",
        ) {
            onThresholdsChange(
                thresholds.copy(
                    minTirePressureKpa = it?.let { v ->
                        if (pressureUnit == dev.foss.expeditiongauge.settings.PressureUnit.KPA) v else v * 6.894757f
                    },
                ),
            )
        }
        AlertField(R.string.alerts_max_temp_c, thresholds.maxTireTempC, "settings_alerts_temp") {
            onThresholdsChange(thresholds.copy(maxTireTempC = it))
        }
        AlertField(
            R.string.alerts_pressure_loss_kpa_min,
            thresholds.rapidPressureLossKpaPerMin,
            "settings_alerts_pressure_loss",
        ) {
            onThresholdsChange(thresholds.copy(rapidPressureLossKpaPerMin = it))
        }
    }
}

@Composable
private fun AlertField(
    labelRes: Int,
    value: Float?,
    testTag: String,
    onValue: (Float?) -> Unit,
) {
    AlertField(
        label = stringResource(labelRes),
        value = value,
        testTag = testTag,
        onValue = onValue,
    )
}

@Composable
private fun AlertField(
    label: String,
    value: Float?,
    testTag: String,
    onValue: (Float?) -> Unit,
) {
    OutlinedTextField(
        value = value?.toString().orEmpty(),
        onValueChange = { text ->
            onValue(text.toFloatOrNull())
        },
        modifier = Modifier.fillMaxWidth().testTag(testTag),
        label = { Text(label) },
        placeholder = { Text(stringResource(R.string.alerts_threshold_off)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
    )
}
