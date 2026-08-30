package dev.foss.expeditiongauge.ui.dashboard

import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.car.gauge.InclinometerStyle
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.hudtile.HudTileId
import dev.foss.expeditiongauge.hudtile.HudTileLayout
import dev.foss.expeditiongauge.ui.orientation.OrientationLayoutSpec
import dev.foss.expeditiongauge.ui.theme.ThemeMode

data class DashboardHudProps(
    val telemetry: TelemetrySnapshot,
    val preset: DashboardPreset,
    val showDriftAngle: Boolean,
    val onCalibrate: () -> Unit,
    val onToggleAttitudeDisplay: () -> Unit = {},
    val onCycleInclinometerStyle: () -> Unit = {},
    val recording: Boolean,
    val crawlingMode: Boolean,
    val tpmsEnabled: Boolean,
    val pressureUnit: PressureUnit,
    val tempUnit: TempUnit,
    val speedUnit: SpeedUnit,
    val attitudeGaugeMode: AttitudeGaugeMode,
    val inclinometerStyle: InclinometerStyle = InclinometerStyle.LADDER,
    val activeAlerts: Set<AlertType>,
    val maxPitchAlertDeg: Float? = null,
    val maxRollAlertDeg: Float? = null,
    val layoutSpec: OrientationLayoutSpec,
    val displayRotation: Int = 0,
    val motionReduced: Boolean = false,
    val highContrast: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.System,
    val isLive: Boolean = false,
    val onMenuClick: () -> Unit = {},
    val onRecordClick: () -> Unit = {},
    val onMarkEvent: () -> Unit = {},
    val onScreenshotClick: () -> Unit = {},
    val onLevelClick: () -> Unit = {},
    val alertsMuted: Boolean = false,
    val onMuteClick: () -> Unit = {},
    val storedDtcs: List<DtcEntry> = emptyList(),
    val canClearDtcs: Boolean = false,
    val onClearDtcs: () -> Unit = {},
    val statusLines: List<String> = emptyList(),
    val tileOrder: List<HudTileId> = HudTileLayout.DEFAULT,
)
