package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import dev.foss.expeditiongauge.settings.SettingsPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/** Collects settings + telemetry into bridge volatile state. */
internal object AndroidAutoBridgeCollectors {
    fun start(
        services: ExpeditionGaugeServices,
        settings: SettingsPreferences,
        scope: CoroutineScope,
        apply: (AndroidAutoBridgeState, AttitudeGaugeMode, Boolean) -> Unit,
    ) {
        scope.launch {
            combine(
                combine(
                    settings.speedUnit,
                    settings.pressureUnit,
                    settings.tempUnit,
                    services.telemetryBus.snapshots,
                ) { spd, pressure, temp, telem ->
                    AndroidAutoTelemetryState(spd, pressure, temp, telem)
                },
                services.alertService.activeAlerts,
                services.alertThresholdsPreferences.thresholds,
                settings.attitudeGaugeMode,
                services.recordingWriter.recording,
            ) { telemState, alerts, thresholds, mode, isRecording ->
                AndroidAutoBridgeState(
                    speedUnit = telemState.speedUnit,
                    pressureUnit = telemState.pressureUnit,
                    tempUnit = telemState.tempUnit,
                    snapshot = telemState.snapshot,
                    alerts = alerts,
                    thresholds = thresholds,
                ) to (mode to isRecording)
            }.collect { (state, modeAndRec) ->
                val (mode, isRecording) = modeAndRec
                FeatureFlags.androidAutoEnabled = FeatureFlags.androidAutoCapable
                apply(state, mode, isRecording)
            }
        }
    }
}
