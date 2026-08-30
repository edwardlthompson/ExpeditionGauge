package dev.foss.expeditiongauge.ui.dashboard.hud

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.car.CarHudTileBuilder
import dev.foss.expeditiongauge.car.gauge.TelemetryCubeLayout
import dev.foss.expeditiongauge.crawlhud.CrawlHudDeclutter
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.shiftlight.ShiftLight
import dev.foss.expeditiongauge.telemetry.SensorLinkState
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.ui.components.gauge.TirePressurePanel
import dev.foss.expeditiongauge.ui.phonehuddtc.PhoneHudDtcFooter
import dev.foss.expeditiongauge.ui.shiftlight.ShiftLightLamp
import dev.foss.expeditiongauge.ui.theme.GaugeRed
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.SpacingSm
import dev.foss.expeditiongauge.ui.theme.ThemeMode

@Composable
@Suppress("UNUSED_PARAMETER")
fun TelemetryHudCube(
    telemetry: TelemetrySnapshot,
    preset: DashboardPreset,
    showDriftAngle: Boolean,
    useMetric: Boolean,
    hideGpsExtras: Boolean,
    activeAlerts: Set<AlertType>,
    storedDtcs: List<DtcEntry> = emptyList(),
    canClearDtcs: Boolean = false,
    onClearDtcs: () -> Unit = {},
    statusLines: List<String> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val labels = CarHudTileBuilder.labels(
        telemetry,
        if (useMetric) SpeedUnit.METRIC else SpeedUnit.IMPERIAL,
        PressureUnit.PSI,
        TempUnit.CELSIUS,
    )
    val coords = labels.coordsLabel.lines().filter { it.isNotBlank() }
    val showHeading = CrawlHudDeclutter.showHeading(hideGpsExtras, preset.showHeading)
    val showAlt = CrawlHudDeclutter.showAltitude(hideGpsExtras, preset.showGps)
    val showGps = preset.showGps && !hideGpsExtras
    val speed = if (preset.showSpeed) labels.speedLabel else ""
    val heading = if (showHeading) labels.headingLabel else ""
    val elev = if (showAlt) labels.altLabel else ""
    val lat = if (showGps) coords.getOrElse(0) { "" } else ""
    val lon = if (showGps) coords.getOrElse(1) { "" } else ""
    val clockLabels = rememberHudClockLabels()
    val clock = if (showGps) {
        stringResource(R.string.gauge_time_date_hud, clockLabels.time, clockLabels.date)
    } else {
        ""
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingSm / 3),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val style = hudCubeTextStyle()
            val measurer = rememberTextMeasurer()
            val rowHpx = (constraints.maxHeight / TelemetryCubeLayout.PHONE_ROW_COUNT).coerceAtLeast(1)
            val maxW = (constraints.maxWidth * 0.92f).toInt().coerceAtLeast(1)
            val maxH = (rowHpx * TelemetryCubeLayout.CONTENT_IN_ROW).toInt().coerceAtLeast(1)
            val ceiling = with(LocalDensity.current) { maxH.toSp().value }
            val sharedSp = remember(speed, heading, elev, lat, lon, clock, maxW, maxH, ceiling, style) {
                fitHudTextSp(
                    measurer,
                    listOf(speed, heading, elev, lat, lon, clock),
                    style,
                    minSp = 8f,
                    maxSp = ceiling,
                    maxWidthPx = maxW,
                    maxHeightPx = maxH,
                )
            }
            Column(Modifier.fillMaxSize()) {
                TelemetryCubeLine(
                    text = speed,
                    color = if (AlertType.SPEED in activeAlerts) GaugeRed else GaugeScaleWhite,
                    sizeSp = sharedSp,
                    modifier = Modifier.weight(1f),
                )
                TelemetryCubeLine(heading, GaugeScaleWhite, sharedSp, Modifier.weight(1f))
                TelemetryCubeLine(elev, GaugeScaleWhite, sharedSp, Modifier.weight(1f))
                TelemetryCubeLine(lat, GaugeScaleWhite, sharedSp, Modifier.weight(1f))
                TelemetryCubeLine(lon, GaugeScaleWhite, sharedSp, Modifier.weight(1f))
                TelemetryCubeLine(clock, GaugeScaleWhite, sharedSp, Modifier.weight(1f))
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TelemetryHudLinkRow(
                        links = SensorLinkState.from(telemetry),
                        fillRow = true,
                        satelliteCount = if (showGps) telemetry.numSatellites ?: 0 else null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TelemetryHudPedalBar(
                        throttlePct = telemetry.throttlePct,
                        lonG = telemetry.lonG,
                        fillRow = true,
                        modifier = Modifier.fillMaxWidth(0.92f)
                            .fillMaxHeight(TelemetryCubeLayout.PEDAL_IN_ROW),
                    )
                }
            }
        }
        ShiftLightLamp(
            active = ShiftLight.active(telemetry.rpm) || AlertType.RPM in activeAlerts,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        PhoneHudDtcFooter(
            entries = storedDtcs,
            canClear = canClearDtcs,
            onClear = onClearDtcs,
            statusLines = statusLines,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun TelemetryCubeLine(
    text: String,
    color: Color,
    sizeSp: Float,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (text.isBlank()) return
        Text(
            text = text,
            color = color,
            style = hudCubeTextStyle().copy(fontSize = sizeSp.sp, lineHeight = (sizeSp * 1.2f).sp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier.fillMaxWidth(0.92f),
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
    storedDtcs: List<DtcEntry> = emptyList(),
    canClearDtcs: Boolean = false,
    onClearDtcs: () -> Unit = {},
    statusLines: List<String> = emptyList(),
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
            storedDtcs = storedDtcs,
            canClearDtcs = canClearDtcs,
            onClearDtcs = onClearDtcs,
            statusLines = statusLines,
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
