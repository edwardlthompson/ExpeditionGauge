package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import dev.foss.expeditiongauge.recordingpreroll.RecordingPreroll
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingWriter(
    private val telemetryBus: TelemetryBus,
    private val database: ExpeditionGaugeDatabase,
    private val scope: CoroutineScope,
    private var logIntervalMs: Long = DEFAULT_LOG_INTERVAL_MS,
    private val storageBudget: SessionStorageBudget? = null,
) {
    private val sessionDao = database.recordingSessionDao()
    private val sampleDao = database.sampleDao()
    private val crawlSmoother = RecordingCrawlSmoother()
    private val peaks = RecordingSessionPeaks()

    private val _activeSessionId = MutableStateFlow<Long?>(null)
    val activeSessionId: StateFlow<Long?> = _activeSessionId.asStateFlow()

    private val _recording = MutableStateFlow(false)
    val recording: StateFlow<Boolean> = _recording.asStateFlow()

    private val _storageBlocked = MutableStateFlow(false)
    val storageBlocked: StateFlow<Boolean> = _storageBlocked.asStateFlow()

    var autoRecordTriggerAddress: String? = null
        private set

    private var writerJob: Job? = null
    private var samplesSincePruneCheck = 0
    private val preroll = ArrayDeque<TelemetrySnapshot>()

    init {
        scope.launch {
            telemetryBus.snapshots.collect { snap ->
                if (!_recording.value) RecordingPreroll.retain(preroll, snap, snap.timestampMs)
            }
        }
    }

    suspend fun startRecording(
        recordingMode: RecordingMode = RecordingMode.NORMAL,
        externalImuConnected: Boolean = false,
        autoRecordTriggerAddress: String? = null,
        manualStart: Boolean = true,
    ): Long {
        if (_recording.value) return _activeSessionId.value ?: -1L
        val hasSpace = storageBudget?.ensureSpaceForNewSession() ?: true
        if (!hasSpace) {
            _storageBlocked.value = true
            throw StorageCapBlockedException()
        }
        _storageBlocked.value = false
        val crawlProfile = if (recordingMode == RecordingMode.CRAWLING) {
            CrawlingModeProfile.forMode(recordingMode, externalImuConnected)
        } else {
            null
        }
        crawlProfile?.let { profile ->
            logIntervalMs = (1000L / profile.effectiveImuRateHz(externalImuConnected))
                .coerceIn(MIN_INTERVAL_MS, MAX_INTERVAL_MS)
        }
        crawlSmoother.reset(crawlProfile)
        samplesSincePruneCheck = 0
        this.autoRecordTriggerAddress = if (manualStart) null else autoRecordTriggerAddress
        val now = System.currentTimeMillis()
        val name = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(now))
        val id = sessionDao.insert(
            RecordingSessionEntity(
                name = name,
                startTimeMs = now,
                recordingMode = recordingMode,
                autoRecordTriggerAddress = if (manualStart) null else autoRecordTriggerAddress,
            ),
        )
        _activeSessionId.value = id
        _recording.value = true
        peaks.reset()
        RecordingPreroll.drain(preroll).forEach { writeSample(id, it) }
        telemetryBus.publish(telemetryBus.snapshots.value.copy(recordingActive = true))
        writerJob = scope.launch { writeLoop(id) }
        return id
    }

    suspend fun stopRecording() {
        writerJob?.cancel()
        writerJob = null
        val id = _activeSessionId.value ?: return
        val session = sessionDao.getById(id) ?: return
        sessionDao.update(
            session.copy(
                endTimeMs = System.currentTimeMillis(),
                deviceConfigJson = peaks.toDeviceConfigJson(),
            ),
        )
        _recording.value = false
        _activeSessionId.value = null
        autoRecordTriggerAddress = null
        crawlSmoother.reset(null)
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
            if (++samplesSincePruneCheck >= PRUNE_CHECK_SAMPLE_INTERVAL) {
                samplesSincePruneCheck = 0
                storageBudget?.pruneOldestUntilUnderCap(excludeSessionId = sessionId)
            }
        }
    }

    private suspend fun writeSample(sessionId: Long, snapshot: TelemetrySnapshot) {
        val smoothed = crawlSmoother.smooth(snapshot)
        peaks.observe(smoothed)
        sampleDao.insert(RecordingSampleWriter.toEntity(smoothed, sessionId))
    }

    companion object {
        const val DEFAULT_LOG_INTERVAL_MS = 20L
        const val MIN_INTERVAL_MS = 10L
        const val MAX_INTERVAL_MS = 1000L
        private const val PRUNE_CHECK_SAMPLE_INTERVAL = 1500
    }
}
