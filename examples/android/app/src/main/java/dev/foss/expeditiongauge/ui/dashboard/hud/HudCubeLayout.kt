package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.ui.components.gauge.AttitudeGMeterGauge
import dev.foss.expeditiongauge.ui.components.gauge.TirePressurePanel
import dev.foss.expeditiongauge.ui.dashboard.DashboardHudProps
import dev.foss.expeditiongauge.ui.dashboard.hud.CombinedTelemetryTpmsCube
import dev.foss.expeditiongauge.ui.dashboard.hud.TelemetryHudCube
import dev.foss.expeditiongauge.ui.orientation.HudTileMode

@Composable
fun HudCubeLayout(
    props: DashboardHudProps,
    modifier: Modifier = Modifier,
) {
    val preset = props.preset
    val telemetry = props.telemetry
    val isPortraitLayout = !props.layoutSpec.isLandscape
    val useMetric = props.speedUnit == SpeedUnit.METRIC

    val gMeterTile: @Composable () -> Unit = {
        if (preset.showAttitude) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    gaugeSizeDp = props.layoutSpec.attitudeGaugeSizeDp.dp,
                    displayRotation = props.displayRotation,
                    recording = props.recording,
                    isPortraitLayout = isPortraitLayout,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    val telemetryTile: @Composable () -> Unit = {
        TelemetryHudCube(
            telemetry = telemetry,
            preset = preset,
            showDriftAngle = props.showDriftAngle,
            useMetric = useMetric,
            hideGpsDetail = props.crawlingMode && props.recording,
            activeAlerts = props.activeAlerts,
            modifier = Modifier.fillMaxSize(),
        )
    }

    val tpmsTile: @Composable () -> Unit = {
        TirePressurePanel(
            frontLeft = telemetry.frontLeftPressure,
            frontRight = telemetry.frontRightPressure,
            rearLeft = telemetry.rearLeftPressure,
            rearRight = telemetry.rearRightPressure,
            pressureUnit = props.pressureUnit,
            tempUnit = props.tempUnit,
            motionReduced = props.motionReduced,
            highContrast = props.highContrast,
            modifier = Modifier.fillMaxSize(),
        )
    }

    val combinedTile: @Composable () -> Unit = {
        CombinedTelemetryTpmsCube(
            telemetry = telemetry,
            preset = preset,
            showDriftAngle = props.showDriftAngle,
            useMetric = useMetric,
            hideGpsDetail = props.crawlingMode && props.recording,
            activeAlerts = props.activeAlerts,
            pressureUnit = props.pressureUnit,
            tempUnit = props.tempUnit,
            motionReduced = props.motionReduced,
            highContrast = props.highContrast,
            showTpms = preset.showTirePressure,
            modifier = Modifier.fillMaxSize(),
        )
    }

    when (props.layoutSpec.tileMode) {
        HudTileMode.THREE_TILE -> {
            val tiles = buildList {
                if (preset.showAttitude) add(gMeterTile)
                if (preset.showSpeed || preset.showHeading || preset.showGps) add(telemetryTile)
                if (preset.showTirePressure) add(tpmsTile)
            }
            HudCubeStack(isPortraitLayout = isPortraitLayout, modifier = modifier, tiles = tiles)
        }
        HudTileMode.TWO_TILE -> {
            val tiles = buildList {
                if (preset.showAttitude) add(gMeterTile)
                add(combinedTile)
            }
            HudCubeStack(isPortraitLayout = isPortraitLayout, modifier = modifier, tiles = tiles)
        }
    }
}
