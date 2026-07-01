package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.ui.components.gauge.GpsReadoutPanel
import dev.foss.expeditiongauge.ui.components.gauge.GpsStatusChip
import dev.foss.expeditiongauge.ui.components.gauge.SpeedHeadingRow
import dev.foss.expeditiongauge.ui.components.gauge.TirePressurePanel
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingSm
import androidx.compose.ui.unit.dp

@Composable
fun TelemetryHudCube(
    telemetry: TelemetrySnapshot,
    preset: DashboardPreset,
    showDriftAngle: Boolean,
    useMetric: Boolean,
    hideGpsExtras: Boolean,
    activeAlerts: Set<AlertType>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = SpacingSm / 4, vertical = SpacingSm / 4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (preset.showSpeed || preset.showHeading) {
            SpeedHeadingRow(
                speedMps = telemetry.speedMps,
                headingDeg = telemetry.headingDeg,
                useMetric = useMetric,
                showSpeed = preset.showSpeed,
                showHeading = preset.showHeading,
                modifier = Modifier.fillMaxWidth(),
            )
            telemetry.rpm?.let { rpm ->
                Text(
                    text = stringResource(R.string.playback_rpm, rpm),
                    color = GaugeScaleWhite,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        if (preset.showGps) {
            GpsStatusChip(
                gpsFix = telemetry.gpsFix,
                gpsSource = telemetry.gpsSource,
                numSatellites = telemetry.numSatellites,
                hdop = telemetry.hdop,
            )
            GpsReadoutPanel(
                latitude = telemetry.latitude,
                longitude = telemetry.longitude,
                altitudeM = telemetry.altitudeM,
                driftAngleDeg = telemetry.driftAngleDeg,
                showDriftAngle = showDriftAngle || preset.emphasizeDrift,
                useMetric = useMetric,
                compact = true,
                showTime = !hideGpsExtras,
                showAltitude = !hideGpsExtras,
            )
        }
        telemetry.slipRatio?.let { slip ->
            Text(
                text = stringResource(R.string.gauge_slip_ratio, slip),
                color = GaugeYellow,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
fun CombinedTelemetryTpmsCube(
    telemetry: TelemetrySnapshot,
    preset: DashboardPreset,
    showDriftAngle: Boolean,
    useMetric: Boolean,
    hideGpsExtras: Boolean,
    activeAlerts: Set<AlertType>,
    pressureUnit: PressureUnit,
    tempUnit: TempUnit,
    motionReduced: Boolean,
    highContrast: Boolean,
    showTpms: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxSize()) {
        TelemetryHudCube(
            telemetry = telemetry,
            preset = preset,
            showDriftAngle = showDriftAngle,
            useMetric = useMetric,
            hideGpsExtras = hideGpsExtras,
            activeAlerts = activeAlerts,
            modifier = Modifier.weight(1f),
        )
        if (showTpms) {
            TirePressurePanel(
                frontLeft = telemetry.frontLeftPressure,
                frontRight = telemetry.frontRightPressure,
                rearLeft = telemetry.rearLeftPressure,
                rearRight = telemetry.rearRightPressure,
                pressureUnit = pressureUnit,
                tempUnit = tempUnit,
                motionReduced = motionReduced,
                highContrast = highContrast,
                compact = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
