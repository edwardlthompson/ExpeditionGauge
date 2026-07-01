package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.ui.orientation.OrientationLayoutEngine
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun DashboardHudLayout(
    telemetry: TelemetrySnapshot,
    preset: DashboardPreset,
    showDriftAngle: Boolean,
    onCalibrate: () -> Unit,
    recording: Boolean = false,
    crawlingMode: Boolean = false,
    tpmsEnabled: Boolean = false,
    pressureUnit: PressureUnit = PressureUnit.PSI,
    tempUnit: TempUnit = TempUnit.CELSIUS,
    speedUnit: SpeedUnit = SpeedUnit.METRIC,
    attitudeGaugeMode: AttitudeGaugeMode = AttitudeGaugeMode.ATTITUDE,
    activeAlerts: Set<AlertType> = emptySet(),
    displayRotation: Int = 0,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val spec = OrientationLayoutEngine.spec(maxWidth.value, maxHeight.value)
        val props = DashboardHudProps(
            telemetry = telemetry,
            preset = preset,
            showDriftAngle = showDriftAngle,
            onCalibrate = onCalibrate,
            recording = recording,
            crawlingMode = crawlingMode,
            tpmsEnabled = tpmsEnabled,
            pressureUnit = pressureUnit,
            tempUnit = tempUnit,
            speedUnit = speedUnit,
            attitudeGaugeMode = attitudeGaugeMode,
            activeAlerts = activeAlerts,
            layoutSpec = spec,
            displayRotation = displayRotation,
        )
        if (spec.isLandscape) {
            DashboardHudLandscape(
                props = props,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            )
        } else {
            DashboardHudPortrait(
                props = props,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            )
        }
    }
}

@Composable
fun DashboardOfflineBanner(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.app_status_offline),
        style = MaterialTheme.typography.bodySmall,
        color = GaugeYellow,
        modifier = modifier.padding(SpacingMd),
    )
}
