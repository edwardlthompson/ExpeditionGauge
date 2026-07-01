package dev.foss.expeditiongauge.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.ui.components.gauge.AttitudeGMeterGauge
import dev.foss.expeditiongauge.ui.components.gauge.GpsReadoutPanel
import dev.foss.expeditiongauge.ui.components.gauge.GpsStatusChip
import dev.foss.expeditiongauge.ui.components.gauge.HeadingReadout
import dev.foss.expeditiongauge.ui.components.gauge.SpeedometerGauge
import dev.foss.expeditiongauge.ui.components.gauge.TirePressurePanel

@Composable
fun DashboardHudPortrait(
    props: DashboardHudProps,
    modifier: Modifier = Modifier,
) {
    val preset = props.preset
    val telemetry = props.telemetry
    val spec = props.layoutSpec
    val attitudeSize = spec.attitudeGaugeSizeDp.dp
    val speedSize = spec.speedometerGaugeSizeDp.dp
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (preset.showAttitude) {
            AttitudeGMeterGauge(
                pitchDeg = telemetry.pitchDeg,
                rollDeg = telemetry.rollDeg,
                latG = telemetry.latG,
                lonG = telemetry.lonG,
                mode = props.attitudeGaugeMode,
                onCalibrate = props.onCalibrate,
                showPeakHold = props.recording,
                peakPitchDeg = telemetry.peakPitchDeg,
                peakRollDeg = telemetry.peakRollDeg,
                peakAbsPitchDeg = telemetry.peakAbsPitchDeg,
                peakAbsRollDeg = telemetry.peakAbsRollDeg,
                pitchAlertActive = AlertType.PITCH in props.activeAlerts,
                rollAlertActive = AlertType.ROLL in props.activeAlerts,
                latGAlertActive = AlertType.LAT_G in props.activeAlerts,
                gaugeSizeDp = attitudeSize,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            if (preset.showSpeed) {
                SpeedometerGauge(
                    speedMps = telemetry.speedMps,
                    speedFromObd = telemetry.speedFromObd,
                    gaugeSizeDp = speedSize,
                    modifier = Modifier.weight(1f),
                )
            }
            if (preset.showHeading || preset.showGps) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (preset.showHeading) {
                        HeadingReadout(headingDeg = telemetry.headingDeg)
                    }
                    if (preset.showGps) {
                        GpsStatusChip(
                            gpsFix = telemetry.gpsFix,
                            gpsSource = telemetry.gpsSource,
                            numSatellites = telemetry.numSatellites,
                            hdop = telemetry.hdop,
                        )
                        if (!spec.useCompactGps) {
                            GpsReadoutPanel(
                                latitude = telemetry.latitude,
                                longitude = telemetry.longitude,
                                altitudeM = telemetry.altitudeM,
                                driftAngleDeg = telemetry.driftAngleDeg,
                                showDriftAngle = props.showDriftAngle || preset.emphasizeDrift,
                            )
                        }
                    }
                }
            }
        }
        if (preset.showTirePressure && props.tpmsEnabled && FeatureFlags.tpmsEnabled) {
            TirePressurePanel(
                frontLeft = telemetry.frontLeftPressure,
                frontRight = telemetry.frontRightPressure,
                rearLeft = telemetry.rearLeftPressure,
                rearRight = telemetry.rearRightPressure,
                pressureUnit = props.pressureUnit,
                tempUnit = props.tempUnit,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
