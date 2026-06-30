package dev.foss.expeditiongauge

import dev.foss.expeditiongauge.accessibility.AccessibilityPreferences
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

internal fun ExpeditionGaugeServices.bindLifecycleFlows(
    scope: CoroutineScope,
    accessibilityPreferences: AccessibilityPreferences,
) {
    scope.launch {
        settingsPreferences.obdPidConfig.collect { obdManager.pidConfig = it }
    }
    scope.launch {
        settingsPreferences.madgwickBeta.collect { beta ->
            fusionEngine.setMadgwickBeta(beta)
        }
    }
    scope.launch {
        settingsPreferences.developerModeEnabled.collect { enabled ->
            FeatureFlags.developerModeEnabled = enabled
        }
    }
    scope.launch {
        settingsPreferences.logIntervalMs.collect { recordingWriter.setLogIntervalMs(it) }
    }
    scope.launch {
        settingsPreferences.tpmsEnabled.collect { enabled ->
            FeatureFlags.tpmsEnabled = enabled
            if (!enabled) bleTpmsManager.stopScan()
        }
    }
    scope.launch {
        combine(
            settingsPreferences.externalGpsEnabled,
            settingsPreferences.externalGpsAddress,
        ) { enabled, address ->
            enabled to address
        }.collect { (enabled, address) ->
            FeatureFlags.externalGpsEnabled = enabled
            if (!enabled) {
                externalGpsManager.disconnect()
            } else if (address != null) {
                externalGpsManager.selectDevice(address)
                externalGpsManager.connect()
            }
        }
    }
    scope.launch {
        combine(recordingWriter.recording, settingsPreferences.tpmsEnabled) { recording, tpms ->
            recording to tpms
        }.collect { (recording, tpms) ->
            if (recording) phoneSensorProvider.resetSessionPeaks()
            if (recording && tpms) bleTpmsManager.startScan()
        }
    }
    scope.launch {
        combine(
            recordingWriter.recording,
            settingsPreferences.lapTimingEnabled,
            telemetryBus.snapshots,
        ) { recording, lapEnabled, snapshot ->
            Triple(recording, lapEnabled, snapshot)
        }.collect { (recording, lapEnabled, snapshot) ->
            if (recording && lapEnabled) {
                lapTimingService.updateLive(snapshot, recording = true)
            }
        }
    }
    scope.launch {
        combine(
            telemetryBus.snapshots,
            recordingWriter.recording,
            recordingWriter.activeSessionId,
            accessibilityPreferences.audibleTonesEnabled,
        ) { snapshot, recording, sessionId, audible ->
            AlertProcessInput(snapshot, recording, sessionId, audible)
        }.collect { input ->
            alertService.process(
                snapshot = input.snapshot,
                sessionId = input.sessionId,
                recording = input.recording,
                audibleEnabled = input.audible,
            )
        }
    }
}

private data class AlertProcessInput(
    val snapshot: TelemetrySnapshot,
    val recording: Boolean,
    val sessionId: Long?,
    val audible: Boolean,
)
