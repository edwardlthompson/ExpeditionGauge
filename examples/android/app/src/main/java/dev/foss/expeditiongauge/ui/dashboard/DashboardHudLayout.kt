package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.gauge.GaugeLogic
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.ui.components.gauge.AttitudeGMeterGauge
import dev.foss.expeditiongauge.ui.components.gauge.GpsReadoutPanel
import dev.foss.expeditiongauge.ui.components.gauge.GpsStatusChip
import dev.foss.expeditiongauge.ui.components.gauge.HeadingReadout
import dev.foss.expeditiongauge.ui.components.gauge.SpeedometerGauge
import dev.foss.expeditiongauge.ui.components.gauge.StatusIcons
import dev.foss.expeditiongauge.ui.components.gauge.TirePressurePanel
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
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
    pressureUnit: dev.foss.expeditiongauge.settings.PressureUnit = dev.foss.expeditiongauge.settings.PressureUnit.PSI,
    tempUnit: dev.foss.expeditiongauge.settings.TempUnit = dev.foss.expeditiongauge.settings.TempUnit.CELSIUS,
    attitudeGaugeMode: dev.foss.expeditiongauge.gauge.AttitudeGaugeMode =
        dev.foss.expeditiongauge.gauge.AttitudeGaugeMode.ATTITUDE,
    activeAlerts: Set<AlertType> = emptySet(),
    modifier: Modifier = Modifier,
) {
    val emphasizeAttitude = preset.weights.attitude > 1f || (crawlingMode && recording)
    val hideGpsDetail = crawlingMode && recording
    val attitudeWeight = if (emphasizeAttitude) preset.weights.attitude.coerceAtLeast(1.5f) else preset.weights.attitude
    val hudDescription = stringResource(
        R.string.gauge_hud_a11y,
        GaugeLogic.formatSignedDegrees(telemetry.pitchDeg),
        GaugeLogic.formatSignedDegrees(telemetry.rollDeg),
        GaugeLogic.formatHeading(telemetry.headingDeg),
        telemetry.latG,
        GaugeLogic.formatSpeedMps(telemetry.speedMps, useMetric = true),
    )
    Row(
        modifier = modifier.semantics { contentDescription = hudDescription },
    ) {
        if (preset.showAttitude && attitudeWeight > 0f) {
            Box(
                modifier = Modifier.weight(attitudeWeight).fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                AttitudeGMeterGauge(
                    pitchDeg = telemetry.pitchDeg,
                    rollDeg = telemetry.rollDeg,
                    latG = telemetry.latG,
                    lonG = telemetry.lonG,
                    mode = attitudeGaugeMode,
                    onCalibrate = onCalibrate,
                    showPeakHold = recording,
                    peakPitchDeg = telemetry.peakPitchDeg,
                    peakRollDeg = telemetry.peakRollDeg,
                    peakAbsPitchDeg = telemetry.peakAbsPitchDeg,
                    peakAbsRollDeg = telemetry.peakAbsRollDeg,
                    pitchAlertActive = AlertType.PITCH in activeAlerts,
                    rollAlertActive = AlertType.ROLL in activeAlerts,
                    latGAlertActive = AlertType.LAT_G in activeAlerts,
                )
            }
        }
        if (preset.showSpeed || preset.showHeading || preset.showGps) {
            Column(
                modifier = Modifier.weight(preset.weights.center.coerceAtLeast(0.1f)).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (preset.showSpeed) {
                    SpeedometerGauge(
                        speedMps = telemetry.speedMps,
                        speedFromObd = telemetry.speedFromObd,
                    )
                    telemetry.rpm?.let { rpm ->
                        Text(
                            text = stringResource(R.string.playback_rpm, rpm),
                            color = GaugeScaleWhite,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                if (preset.showHeading) {
                    HeadingReadout(headingDeg = telemetry.headingDeg)
                    Text(
                        text = stringResource(R.string.gauge_lat_g, telemetry.latG),
                        color = if (AlertType.LAT_G in activeAlerts) GaugeRed else GaugeScaleWhite,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (preset.showGps) {
                    GpsStatusChip(
                        gpsFix = telemetry.gpsFix,
                        gpsSource = telemetry.gpsSource,
                        numSatellites = telemetry.numSatellites,
                        hdop = telemetry.hdop,
                    )
                    if (!hideGpsDetail) {
                        GpsReadoutPanel(
                            latitude = telemetry.latitude,
                            longitude = telemetry.longitude,
                            altitudeM = telemetry.altitudeM,
                            driftAngleDeg = telemetry.driftAngleDeg,
                            showDriftAngle = showDriftAngle || preset.emphasizeDrift,
                        )
                    }
                }
                telemetry.slipRatio?.let { slip ->
                    Text(
                        text = stringResource(R.string.gauge_slip_ratio, slip),
                        color = GaugeYellow,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                telemetry.rearSlipRatio?.let { rearSlip ->
                    Text(
                        text = stringResource(R.string.gauge_rear_slip_ratio, rearSlip),
                        color = GaugeYellow,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        if (preset.showTirePressure && tpmsEnabled && FeatureFlags.tpmsEnabled) {
            Column(modifier = Modifier.weight(preset.weights.side.coerceAtLeast(0.1f)).fillMaxHeight()) {
                TirePressurePanel(
                    frontLeft = telemetry.frontLeftPressure,
                    frontRight = telemetry.frontRightPressure,
                    rearLeft = telemetry.rearLeftPressure,
                    rearRight = telemetry.rearRightPressure,
                    pressureUnit = pressureUnit,
                    tempUnit = tempUnit,
                    modifier = Modifier.weight(1f),
                )
                StatusIcons(gpsFix = telemetry.gpsFix, batteryVoltage = telemetry.batteryVoltage)
            }
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
