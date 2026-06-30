package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
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

    suspend fun startRecording(): Long {
        if (_recording.value) return _activeSessionId.value ?: -1L
        val now = System.currentTimeMillis()
        val name = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(now))
        val id = sessionDao.insert(RecordingSessionEntity(name = name, startTimeMs = now))
        _activeSessionId.value = id
        _recording.value = true
        peakSpeed = 0f
        peakDrift = 0f
        telemetryBus.publish(telemetryBus.snapshots.value.copy(recordingActive = true))
        writerJob = scope.launch { writeLoop(id) }
        return id
    }

    suspend fun stopRecording() {
        writerJob?.cancel()
        writerJob = null
        val id = _activeSessionId.value ?: return
        val session = sessionDao.getById(id) ?: return
        sessionDao.update(session.copy(endTimeMs = System.currentTimeMillis()))
        _recording.value = false
        _activeSessionId.value = null
        telemetryBus.publish(telemetryBus.snapshots.value.copy(recordingActive = false))
    }

    fun setLogIntervalMs(intervalMs: Long) {
        logIntervalMs = intervalMs.coerceIn(MIN_INTERVAL_MS, MAX_INTERVAL_MS)
    }

    private suspend fun writeLoop(sessionId: Long) {
        while (scope.coroutineContext.isActive && _recording.value) {
            writeSample(sessionId, telemetryBus.snapshots.value)
            delay(logIntervalMs)
        }
    }

    private suspend fun writeSample(sessionId: Long, snapshot: TelemetrySnapshot) {
        peakSpeed = maxOf(peakSpeed, snapshot.speedMps)
        snapshot.driftAngleDeg?.let { peakDrift = maxOf(peakDrift, abs(it)) }
        sampleDao.insert(snapshot.toEntity(sessionId))
    }

    private fun TelemetrySnapshot.toEntity(sessionId: Long): SampleEntity {
        val extras = JSONObject().apply {
            put("fusionSource", fusionSource)
            put("gpsSource", gpsSource)
            hdop?.let { put("hdop", it) }
            numSatellites?.let { put("numSatellites", it) }
            chassisTwistDeg?.let { put("chassisTwistDeg", it) }
            rearSlipRatio?.let { put("rearSlipRatio", it) }
            tpms?.let { tp ->
                put("tpms", JSONObject().apply {
                    listOf("fl", "fr", "rl", "rr").zip(
                        listOf(tp.frontLeft, tp.frontRight, tp.rearLeft, tp.rearRight),
                    ).forEach { (key, corner) ->
                        put(key, JSONObject().apply {
                            corner.pressureKpa?.let { put("pressureKpa", it) }
                            corner.tempC?.let { put("tempC", it) }
                            corner.batteryPct?.let { put("batteryPct", it) }
                            put("lastSeenMs", corner.lastSeenMs)
                        })
                    }
                })
            }
            if (imuStatuses.isNotEmpty()) {
                put("imu", imuStatuses.joinToString(",") { "${it.deviceId}:${it.placement}" })
            }
        }
        return SampleEntity(
            sessionId = sessionId,
            timestampMs = timestampMs,
            latitude = latitude,
            longitude = longitude,
            altitudeM = altitudeM,
            speedMps = speedMps,
            headingDeg = headingDeg,
            pitchDeg = pitchDeg,
            rollDeg = rollDeg,
            lonAccel = lonG,
            latG = latG,
            yawRate = 0f,
            driftAngleDeg = driftAngleDeg,
            bodyYawDeg = bodyYawDeg,
            velocityHeadingDeg = velocityHeadingDeg,
            throttle = throttlePct,
            rpm = rpm,
            load = engineLoadPct,
            slipRatio = slipRatio,
            extrasJson = extras.toString(),
        )
    }

    companion object {
        const val DEFAULT_LOG_INTERVAL_MS = 20L
        const val MIN_INTERVAL_MS = 10L
        const val MAX_INTERVAL_MS = 1000L
    }
}
