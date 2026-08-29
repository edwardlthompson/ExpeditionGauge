package dev.foss.expeditiongauge.car

import android.graphics.Bitmap
import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.alerts.AlertType
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import dev.foss.expeditiongauge.settings.PressureUnit
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.settings.TempUnit
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Mute collect + setAlertsMuted helpers for [AndroidAutoBridge]. */
internal class AndroidAutoBridgeMute(
    private val accessibility: AccessibilityPreferences,
    private val scope: CoroutineScope,
    private val onMutedChanged: (Boolean) -> Unit,
    private val onHighContrastChanged: (Boolean) -> Unit = {},
) {
    fun startCollect() {
        scope.launch {
            accessibility.alertsMuted.collect { muted ->
                onMutedChanged(muted)
            }
        }
        scope.launch {
            accessibility.highContrastEnabled.collect { enabled ->
                onHighContrastChanged(enabled)
            }
        }
    }

    fun setAlertsMuted(muted: Boolean): Boolean {
        scope.launch {
            accessibility.setAlertsMuted(muted)
        }
        onMutedChanged(muted)
        return true
    }
}

/** composeHud / bitmap / tile helpers for [AndroidAutoBridge]. */
internal class AndroidAutoBridgeHudCompose(
    appContext: android.content.Context,
) {
    private val hudComposer = AaHudComposer(appContext)

    fun composeHud(
        snapshot: TelemetrySnapshot,
        attitudeGaugeMode: AttitudeGaugeMode,
        activeAlerts: Set<AlertType>,
        alertThresholds: AlertThresholds,
        displaySpec: AaDisplaySpec,
        speedUnit: SpeedUnit,
        pressureUnit: PressureUnit,
        tempUnit: TempUnit,
        cubePxOverride: Int?,
        orientation: HudStripOrientation = HudStripOrientation.ROW,
        storedDtcs: List<DtcEntry> = emptyList(),
    ): DriveHudContent = hudComposer.composeDriveHud(
        snapshot, attitudeGaugeMode, activeAlerts, alertThresholds, displaySpec,
        speedUnit = speedUnit, pressureUnit = pressureUnit, tempUnit = tempUnit,
        cubePxOverride = cubePxOverride,
        orientation = orientation,
        storedDtcs = storedDtcs,
    )

    fun snapshotBitmap(): Bitmap? = hudComposer.snapshotBitmap()

    fun hudTiles(
        snapshot: TelemetrySnapshot,
        speedUnit: SpeedUnit,
        pressureUnit: PressureUnit,
        tempUnit: TempUnit,
    ): CarHudTiles {
        val labels = CarHudTileBuilder.labels(snapshot, speedUnit, pressureUnit, tempUnit)
        return CarHudTiles(
            gMeter = CarHudTile("Attitude", labels.attitudeLine, ""),
            telemetry = CarHudTile("Telemetry", labels.telemetrySecondary, ""),
            tpms = CarHudTile("TPMS", labels.tpmsSecondary, ""),
        )
    }
}
