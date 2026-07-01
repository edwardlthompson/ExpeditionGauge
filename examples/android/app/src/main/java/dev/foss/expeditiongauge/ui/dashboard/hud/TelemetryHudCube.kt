package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.ui.components.gauge.GpsReadoutPanel
import dev.foss.expeditiongauge.ui.components.gauge.SpeedHeadingRow
import dev.foss.expeditiongauge.ui.components.gauge.TirePressurePanel
import dev.foss.expeditiongauge.ui.theme.SpacingSm
import dev.foss.expeditiongauge.ui.theme.ThemeMode
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
            .padding(horizontal = SpacingSm / 3, vertical = SpacingSm / 3),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        if (preset.showSpeed || preset.showHeading) {
            SpeedHeadingRow(
                speedMps = telemetry.speedMps,
                headingDeg = telemetry.headingDeg,
                useMetric = useMetric,
                showSpeed = preset.showSpeed,
                showHeading = preset.showHeading,
                enlarged = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (preset.showGps) {
            TelemetryHudMetaRow(
                altitudeM = telemetry.altitudeM,
                useMetric = useMetric,
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
                showDriftAngle = false,
                useMetric = useMetric,
                compact = false,
                hudCube = true,
                showTime = false,
                showAltitude = false,
            )
        }
        if (preset.showAttitude) {
            TelemetryHudAttitudeRow(
                pitchDeg = telemetry.pitchDeg,
                rollDeg = telemetry.rollDeg,
                showDriftAngle = showDriftAngle || preset.emphasizeDrift,
                driftAngleDeg = telemetry.driftAngleDeg,
            )
        }
        TelemetryHudVehicleRow(
            rpm = telemetry.rpm,
            batteryVoltage = telemetry.batteryVoltage,
            latG = telemetry.latG,
            lonG = telemetry.lonG,
            slipRatio = telemetry.slipRatio,
        )
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
    themeMode: ThemeMode = ThemeMode.System,
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
                themeMode = themeMode,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
