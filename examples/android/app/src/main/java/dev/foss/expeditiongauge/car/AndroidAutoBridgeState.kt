package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

internal data class AndroidAutoTelemetryState(
    val speedUnit: SpeedUnit,
    val pressureUnit: PressureUnit,
    val tempUnit: TempUnit,
    val snapshot: TelemetrySnapshot,
)

internal data class AndroidAutoBridgeState(
    val speedUnit: SpeedUnit,
    val pressureUnit: PressureUnit,
    val tempUnit: TempUnit,
    val snapshot: TelemetrySnapshot,
    val alerts: Set<AlertType>,
    val thresholds: AlertThresholds,
)
