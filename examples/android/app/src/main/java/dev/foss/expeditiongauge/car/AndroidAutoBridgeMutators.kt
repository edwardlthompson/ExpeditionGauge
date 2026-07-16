package dev.foss.expeditiongauge.car

import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.recording.StorageCapBlockedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Async, non-throwing car action helpers for [AndroidAutoBridge]. */
internal class AndroidAutoBridgeMutators(
    private val services: ExpeditionGaugeServices,
    private val scope: CoroutineScope,
    private val toast: () -> ((String) -> Unit)?,
    private val invalidateForce: () -> Unit,
) {
    fun startRecording(alreadyRecording: Boolean): Boolean {
        if (alreadyRecording) return true
        scope.launch(Dispatchers.IO) {
            val result = runCatching { services.recordingWriter.startRecording() }
            withContext(Dispatchers.Main.immediate) {
                if (result.isFailure) {
                    val msg = if (result.exceptionOrNull() is StorageCapBlockedException) {
                        "Storage full — free a session"
                    } else {
                        "Record failed"
                    }
                    toast()?.invoke(msg)
                }
                invalidateForce()
            }
        }
        return true
    }

    fun stopRecording(isRecording: Boolean): Boolean {
        if (!isRecording) return true
        scope.launch(Dispatchers.IO) {
            runCatching { services.recordingWriter.stopRecording() }
            withContext(Dispatchers.Main.immediate) { invalidateForce() }
        }
        return true
    }

    fun markEvent(): Boolean {
        val sessionId = services.recordingWriter.activeSessionId.value ?: return false
        scope.launch(Dispatchers.IO) {
            runCatching {
                services.sessionEventRecorder.markEvent(
                    sessionId,
                    services.telemetryBus.snapshots.value,
                )
            }
        }
        return true
    }

    fun zeroAttitude(
        fusionSource: String,
        pitchDeg: Float,
        rollDeg: Float,
        yawDeg: Float,
    ): Boolean {
        if (fusionSource != "phone") return false
        scope.launch(Dispatchers.IO) {
            runCatching {
                services.autocalibrationController.manualZero(
                    pitchDeg = pitchDeg,
                    rollDeg = rollDeg,
                    yawDeg = yawDeg,
                    displayRotation = 0,
                )
            }.onFailure {
                withContext(Dispatchers.Main.immediate) { toast()?.invoke("Zero failed") }
            }
            withContext(Dispatchers.Main.immediate) { invalidateForce() }
        }
        return true
    }
}
