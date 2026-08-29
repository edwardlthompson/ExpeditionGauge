package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.gauge.isInclinometerStyle
import dev.foss.expeditiongauge.gauge.toInclinometerStyle
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.ui.components.gauge.AttitudeGMeterGauge
import dev.foss.expeditiongauge.ui.components.gauge.CompassBallGauge
import dev.foss.expeditiongauge.ui.components.gauge.InclinometerGauge
import dev.foss.expeditiongauge.ui.components.gauge.TirePressurePanel
import dev.foss.expeditiongauge.ui.dashboard.DashboardHudProps
import dev.foss.expeditiongauge.ui.dashboard.DashboardHudSideChrome
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
    val hideGpsExtras = props.crawlingMode && props.recording

    val gMeterTile: @Composable () -> Unit = {
        if (preset.showAttitude) {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when {
                    props.attitudeGaugeMode.isInclinometerStyle() -> InclinometerGauge(
                        pitchDeg = telemetry.pitchDeg,
                        rollDeg = telemetry.rollDeg,
                        onCalibrate = props.onCalibrate,
                        style = props.attitudeGaugeMode.toInclinometerStyle()
                            ?: props.inclinometerStyle,
                        onCycleStyle = null,
                        onToggleDisplay = props.onToggleAttitudeDisplay,
                        isPortraitLayout = isPortraitLayout,
                        displayRotation = props.displayRotation,
                        pitchAlertActive = AlertType.PITCH in props.activeAlerts,
                        rollAlertActive = AlertType.ROLL in props.activeAlerts,
                        maxPitchThresholdDeg = props.maxPitchAlertDeg,
                        maxRollThresholdDeg = props.maxRollAlertDeg,
                        yawDeg = telemetry.bodyYawDeg ?: telemetry.headingDeg,
                        latG = telemetry.latG,
                        lonG = telemetry.lonG,
                        gaugeSizeDp = props.layoutSpec.attitudeGaugeSizeDp.dp,
                        modifier = Modifier.fillMaxSize(),
                    )
                    props.attitudeGaugeMode == AttitudeGaugeMode.COMPASS_BALL -> CompassBallGauge(
                        pitchDeg = telemetry.pitchDeg,
                        rollDeg = telemetry.rollDeg,
                        bodyYawDeg = telemetry.bodyYawDeg,
                        headingDeg = telemetry.headingDeg,
                        onCalibrate = props.onCalibrate,
                        onToggleDisplay = props.onToggleAttitudeDisplay,
                        modifier = Modifier.fillMaxSize(),
                    )
                    else -> AttitudeGMeterGauge(
                        pitchDeg = telemetry.pitchDeg,
                        rollDeg = telemetry.rollDeg,
                        latG = telemetry.latG,
                        lonG = telemetry.lonG,
                        mode = props.attitudeGaugeMode,
                        onCalibrate = props.onCalibrate,
                        onToggleDisplay = props.onToggleAttitudeDisplay,
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
                        highContrast = true,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }

    val telemetryTile: @Composable () -> Unit = {
        TelemetryHudCube(
            telemetry = telemetry,
            preset = preset,
            showDriftAngle = props.showDriftAngle,
            useMetric = useMetric,
            hideGpsExtras = hideGpsExtras,
            activeAlerts = props.activeAlerts,
            storedDtcs = props.storedDtcs,
            canClearDtcs = props.canClearDtcs,
            onClearDtcs = props.onClearDtcs,
            imLine = props.imLine,
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
            highContrast = true,
            themeMode = props.themeMode,
            modifier = Modifier.fillMaxSize(),
        )
    }

    val combinedTile: @Composable () -> Unit = {
        CombinedTelemetryTpmsCube(
            telemetry = telemetry,
            preset = preset,
            showDriftAngle = props.showDriftAngle,
            useMetric = useMetric,
            hideGpsExtras = hideGpsExtras,
            activeAlerts = props.activeAlerts,
            pressureUnit = props.pressureUnit,
            tempUnit = props.tempUnit,
            motionReduced = props.motionReduced,
            highContrast = true,
            themeMode = props.themeMode,
            showTpms = preset.showTirePressure,
            storedDtcs = props.storedDtcs,
            canClearDtcs = props.canClearDtcs,
            onClearDtcs = props.onClearDtcs,
            imLine = props.imLine,
            modifier = Modifier.fillMaxSize(),
        )
    }

    val tiles = when (props.layoutSpec.tileMode) {
        HudTileMode.THREE_TILE -> buildList {
            if (preset.showAttitude) add(gMeterTile)
            if (preset.showSpeed || preset.showHeading || preset.showGps) add(telemetryTile)
            if (preset.showTirePressure) add(tpmsTile)
        }
        HudTileMode.TWO_TILE -> buildList {
            if (preset.showAttitude) add(gMeterTile)
            add(combinedTile)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val count = tiles.size.coerceAtLeast(1)
        val minGutter = 40.dp
        val gutterPad = 4.dp
        val t0 = if (isPortraitLayout) {
            minOf(maxWidth, maxHeight / count)
        } else {
            minOf(maxWidth / count, maxHeight)
        }
        val stack0 = if (isPortraitLayout) t0 else t0 * count
        val naturalGutter = ((maxWidth - stack0) / 2f).coerceAtLeast(0.dp)
        val gutter = maxOf(naturalGutter, minGutter)
        val iconSize = (gutter - gutterPad * 2).coerceIn(28.dp, 48.dp)

        HudCubeStack(
            isPortraitLayout = isPortraitLayout,
            horizontalInset = gutter,
            modifier = Modifier.fillMaxSize(),
            tiles = tiles,
        )
        DashboardHudSideChrome(
            recording = props.recording,
            isLive = props.isLive,
            onMenuClick = props.onMenuClick,
            onRecordClick = props.onRecordClick,
            onMarkEvent = props.onMarkEvent,
            onScreenshotClick = props.onScreenshotClick,
            iconSize = iconSize,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(gutter)
                .padding(horizontal = gutterPad),
        )
    }
}
