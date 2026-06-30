package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class RecordingWriter(
    private val telemetryBus: TelemetryBus,
    private val database: ExpeditionGaugeDatabase,
    private val scope: CoroutineScope,
    private var logIntervalMs: Long = DEFAULT_LOG_INTERVAL_MS,
) {
    private val sessionDao = database.recordingSessionDao()
    private val sampleDao = database.sampleDao()

    private val _activeSessionId = MutableStateFlow<Long?>(null)
    val activeSessionId: StateFlow<Long?> = _activeSessionId.asStateFlow()

    private val _recording = MutableStateFlow(false)
    val recording: StateFlow<Boolean> = _recording.asStateFlow()

    private var writerJob: Job? = null
    private var peakSpeed = 0f
    private var peakDrift = 0f
    private var peakAbsPitch = 0f
    private var peakAbsRoll = 0f
    private var crawlProfile: CrawlingModeProfile? = null
    private val speedSamples = ArrayDeque<Pair<Long, Float>>()

    suspend fun startRecording(
        recordingMode: RecordingMode = RecordingMode.NORMAL,
        externalImuConnected: Boolean = false,
    ): Long {
        if (_recording.value) return _activeSessionId.value ?: -1L
        crawlProfile = if (recordingMode == RecordingMode.CRAWLING) {
            CrawlingModeProfile.forMode(recordingMode, externalImuConnected)
        } else {
            null
        }
        crawlProfile?.let { profile ->
            val hz = profile.effectiveImuRateHz(externalImuConnected)
            logIntervalMs = (1000L / hz).coerceIn(MIN_INTERVAL_MS, MAX_INTERVAL_MS)
        }
        speedSamples.clear()
        val now = System.currentTimeMillis()
        val name = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(now))
        val id = sessionDao.insert(
            RecordingSessionEntity(
                name = name,
                startTimeMs = now,
                recordingMode = recordingMode,
            ),
        )
        _activeSessionId.value = id
        _recording.value = true
        peakSpeed = 0f
        peakDrift = 0f
        peakAbsPitch = 0f
        peakAbsRoll = 0f
        telemetryBus.publish(telemetryBus.snapshots.value.copy(recordingActive = true))
        writerJob = scope.launch { writeLoop(id) }
        return id
    }

    suspend fun stopRecording() {
        writerJob?.cancel()
        writerJob = null
        val id = _activeSessionId.value ?: return
        val session = sessionDao.getById(id) ?: return
        val sessionPeaks = JSONObject().apply {
            put("peakAbsPitchDeg", peakAbsPitch)
            put("peakAbsRollDeg", peakAbsRoll)
            put("peakSpeedMps", peakSpeed)
            put("peakDriftDeg", peakDrift)
        }
        sessionDao.update(
            session.copy(
                endTimeMs = System.currentTimeMillis(),
                deviceConfigJson = sessionPeaks.toString(),
            ),
        )
        _recording.value = false
        _activeSessionId.value = null
        crawlProfile = null
        speedSamples.clear()
        telemetryBus.publish(telemetryBus.snapshots.value.copy(recordingActive = false))
    }

    fun setLogIntervalMs(intervalMs: Long) {
        logIntervalMs = intervalMs.coerceIn(MIN_INTERVAL_MS, MAX_INTERVAL_MS)
    }

    private suspend fun writeLoop(sessionId: Long) {
        var lastWriteMs = 0L
        telemetryBus.snapshots.collectLatest { snapshot ->
            if (!scope.coroutineContext.isActive || !_recording.value) return@collectLatest
            val now = System.currentTimeMillis()
            if (now - lastWriteMs < logIntervalMs) return@collectLatest
            writeSample(sessionId, snapshot)
            lastWriteMs = now
        }
    }

    private suspend fun writeSample(sessionId: Long, snapshot: TelemetrySnapshot) {
        val smoothed = smoothSpeedIfCrawling(snapshot)
        peakSpeed = maxOf(peakSpeed, smoothed.speedMps)
        smoothed.driftAngleDeg?.let { peakDrift = maxOf(peakDrift, abs(it)) }
        peakAbsPitch = maxOf(peakAbsPitch, smoothed.peakAbsPitchDeg)
        peakAbsRoll = maxOf(peakAbsRoll, smoothed.peakAbsRollDeg)
        sampleDao.insert(RecordingSampleWriter.toEntity(smoothed, sessionId))
    }

    private fun smoothSpeedIfCrawling(snapshot: TelemetrySnapshot): TelemetrySnapshot {
        val profile = crawlProfile ?: return snapshot
        val now = snapshot.timestampMs
        speedSamples.addLast(now to snapshot.speedMps)
        val cutoff = now - profile.gpsSmoothingWindowMs
        while (speedSamples.isNotEmpty() && speedSamples.first().first < cutoff) {
            speedSamples.removeFirst()
        }
        if (speedSamples.isEmpty()) return snapshot
        val avg = speedSamples.map { it.second }.average().toFloat()
        return snapshot.copy(speedMps = avg)
    }

    companion object {
        const val DEFAULT_LOG_INTERVAL_MS = 20L
        const val MIN_INTERVAL_MS = 10L
        const val MAX_INTERVAL_MS = 1000L
    }
}
