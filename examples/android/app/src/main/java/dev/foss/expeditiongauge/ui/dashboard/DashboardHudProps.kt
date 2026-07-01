package dev.foss.expeditiongauge.ui.dashboard

import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.ui.orientation.OrientationLayoutSpec

data class DashboardHudProps(
    val telemetry: TelemetrySnapshot,
    val preset: DashboardPreset,
    val showDriftAngle: Boolean,
    val onCalibrate: () -> Unit,
    val recording: Boolean,
    val crawlingMode: Boolean,
    val tpmsEnabled: Boolean,
    val pressureUnit: PressureUnit,
    val tempUnit: TempUnit,
    val speedUnit: SpeedUnit,
    val attitudeGaugeMode: AttitudeGaugeMode,
    val activeAlerts: Set<AlertType>,
    val layoutSpec: OrientationLayoutSpec,
    val displayRotation: Int = 0,
)
