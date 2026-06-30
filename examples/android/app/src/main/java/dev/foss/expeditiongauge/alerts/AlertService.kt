package dev.foss.expeditiongauge.alerts

import android.content.Context
import android.util.Log
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.data.db.dao.AlertEventDao
import dev.foss.expeditiongauge.data.db.entities.AlertEventEntity
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertService(
    context: Context,
    private val alertEventDao: AlertEventDao,
    thresholdsPreferences: AlertThresholdsPreferences,
    scope: CoroutineScope,
) {
    private val engine = AlertEngine()
    private val feedback = AlertFeedback(context)
    private val tpmsTracker = TpmsPressureTracker()
    private val _activeAlerts = MutableStateFlow<Set<AlertType>>(emptySet())
    val activeAlerts: StateFlow<Set<AlertType>> = _activeAlerts.asStateFlow()

    init {
        scope.launch {
            thresholdsPreferences.thresholds.collect { engine.updateThresholds(it) }
        }
    }

    suspend fun process(
        snapshot: TelemetrySnapshot,
        sessionId: Long?,
        recording: Boolean,
        audibleEnabled: Boolean,
    ) {
        if (!FeatureFlags.alertsEnabled) {
            _activeAlerts.value = emptySet()
            return
        }
        val nowMs = snapshot.timestampMs.takeIf { it > 0L } ?: System.currentTimeMillis()
        val events = buildList {
            addAll(engine.evaluate(snapshot, nowMs))
            if (snapshot.obdConnected) {
                addAll(
                    engine.evaluateObd(
                        rpm = snapshot.rpm,
                        slipRatio = snapshot.slipRatio,
                        fuelRateLph = null,
                        speedMps = snapshot.speedMps,
                        nowMs = nowMs,
                    ),
                )
            }
            snapshot.tpms?.let { addAll(engine.evaluateTpms(it, nowMs, tpmsTracker)) }
        }
        _activeAlerts.value = events.map { it.type }.toSet()
        events.forEach { event ->
            Log.d(TAG, "fired type=${event.type} value=${event.value} threshold=${event.threshold}")
            feedback.onAlert(event.type, playTone = audibleEnabled)
            if (recording && sessionId != null) {
                alertEventDao.insert(
                    AlertEventEntity(
                        sessionId = sessionId,
                        timestampMs = event.timestampMs,
                        alertType = event.type.name,
                        value = event.value,
                        threshold = event.threshold,
                    ),
                )
            }
        }
    }

    fun release() = feedback.release()

    private companion object {
        const val TAG = "ExpeditionGauge/Alerts"
    }
}
