package dev.foss.expeditiongauge.alerts

import android.content.Context
import android.os.SystemClock
import android.util.Log
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.data.db.dao.AlertEventDao
import dev.foss.expeditiongauge.data.db.entities.AlertEventEntity
import dev.foss.expeditiongauge.alertsnooze.AlertSnooze
import dev.foss.expeditiongauge.hapticalerts.HapticOverLimit
import dev.foss.expeditiongauge.presetalerts.PresetAlertThresholds
import dev.foss.expeditiongauge.settings.AlertSnoozeStore
import dev.foss.expeditiongauge.settings.HapticAlertsStore
import dev.foss.expeditiongauge.settings.SettingsProfileRepository
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AlertService(
    private val context: Context,
    private val alertEventDao: AlertEventDao,
    thresholdsPreferences: AlertThresholdsPreferences,
    profileRepository: SettingsProfileRepository,
    scope: CoroutineScope,
) {
    private val engine = AlertEngine()
    private val feedback = AlertFeedback(context)
    private val tts = AlertTts(context)
    private val tpmsTracker = TpmsPressureTracker()
    private val _activeAlerts = MutableStateFlow<Set<AlertType>>(emptySet())
    val activeAlerts: StateFlow<Set<AlertType>> = _activeAlerts.asStateFlow()
    private val _activeTireCorners = MutableStateFlow<Set<TireCornerId>>(emptySet())
    val activeTireCorners: StateFlow<Set<TireCornerId>> = _activeTireCorners.asStateFlow()
    private var previouslyActiveKeys = emptySet<String>()
    private val startedAtElapsedMs = SystemClock.elapsedRealtime()
    private val attitudeSettleGate = AttitudeSettleGate()
    private val hapticStore = HapticAlertsStore(context)
    private val snoozeStore = AlertSnoozeStore(context)
    @Volatile
    private var hapticEnabled = true
    @Volatile
    private var snoozeUntil = emptyMap<AlertType, Long>()

    init {
        scope.launch {
            combine(
                thresholdsPreferences.thresholds,
                profileRepository.activeProfile,
            ) { global, profile ->
                PresetAlertThresholds.resolve(profile.presetId, global)
            }.collect { engine.updateThresholds(it) }
        }
        scope.launch {
            hapticStore.enabled.collect { hapticEnabled = it }
        }
        scope.launch {
            snoozeStore.untilByType.collect { snoozeUntil = it }
        }
    }

    suspend fun process(
        snapshot: TelemetrySnapshot,
        sessionId: Long?,
        recording: Boolean,
        audibleEnabled: Boolean,
        audioMode: AlertAudioMode = AlertAudioMode.BEEP,
        muted: Boolean = false,
    ) {
        if (!FeatureFlags.alertsEnabled) {
            _activeAlerts.value = emptySet()
            _activeTireCorners.value = emptySet()
            return
        }
        val nowMs = snapshot.timestampMs.takeIf { it > 0L } ?: System.currentTimeMillis()
        val attitudeSettled = attitudeSettleGate.onSample(snapshot.pitchDeg, snapshot.rollDeg, nowMs)
        val active = buildList {
            addAll(engine.evaluateActive(snapshot, nowMs))
            if (snapshot.obdConnected) {
                addAll(
                    engine.evaluateActiveObd(
                        rpm = snapshot.rpm,
                        fuelRateLph = null,
                        speedMps = snapshot.speedMps,
                        nowMs = nowMs,
                    ),
                )
            }
            snapshot.tpms?.let { addAll(engine.evaluateActiveTpms(it, nowMs, tpmsTracker)) }
        }
        _activeAlerts.value = active.map { it.type }.toSet()
        _activeTireCorners.value = active.mapNotNull { it.tireCorner }.toSet()

        val elapsedMs = SystemClock.elapsedRealtime() - startedAtElapsedMs
        val feedbackCandidates = active.filterNot { event ->
            AlertStartupGrace.suppressFeedback(event.type, elapsedMs, attitudeSettled)
        }
        val feedbackEvents = engine.filterFeedback(feedbackCandidates, nowMs)
        val playAudio = audibleEnabled && !muted
        feedbackEvents.forEach { event ->
            Log.d(
                TAG,
                "feedback type=${event.type} corner=${event.tireCorner} mode=$audioMode " +
                    "playAudio=$playAudio audible=$audibleEnabled muted=$muted",
            )
            val key = "${event.type}:${event.tireCorner?.key.orEmpty()}"
            val isEdge = key !in previouslyActiveKeys
            if (!AlertSnooze.suppressed(snoozeUntil[event.type], nowMs)) {
                feedback.onAlert(
                    event.type,
                    playTone = playAudio && audioMode == AlertAudioMode.BEEP,
                    haptic = HapticOverLimit.shouldVibrate(hapticEnabled, overLimit = true),
                )
                if (playAudio && audioMode == AlertAudioMode.TTS) {
                    tts.speak(AlertPhrases.phrase(context, event))
                }
            }
            if (recording && sessionId != null && isEdge) {
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
        previouslyActiveKeys = active.map { "${it.type}:${it.tireCorner?.key.orEmpty()}" }.toSet()
    }

    fun release() {
        feedback.release()
        tts.release()
    }

    private companion object {
        const val TAG = "ExpeditionGauge/Alerts"
    }
}
