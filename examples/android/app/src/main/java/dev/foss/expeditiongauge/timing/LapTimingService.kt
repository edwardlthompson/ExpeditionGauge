package dev.foss.expeditiongauge.timing

import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity
import dev.foss.expeditiongauge.data.db.entities.TrackConfigEntity
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

data class LapTimingSummary(
    val laps: List<LapEntity>,
    val sessionBestMs: Long?,
    val theoreticalBestMs: Long,
    val splitsByLap: Map<Long, List<SectorSplitEntity>>,
)

class LapTimingService(
    private val database: ExpeditionGaugeDatabase,
    private val settingsPreferences: SettingsPreferences,
) {
    private val trackConfigDao = database.trackConfigDao()
    private val sampleDao = database.sampleDao()
    private val lapDao = database.lapDao()
    private val sectorSplitDao = database.sectorSplitDao()

    private val _liveState = MutableStateFlow(PredictiveTimingState())
    val liveState: StateFlow<PredictiveTimingState> = _liveState.asStateFlow()

    private var predictiveEngine: PredictiveTimingEngine? = null
    private var prevSample: dev.foss.expeditiongauge.data.db.entities.SampleEntity? = null
    private var bestSplits: List<Long> = emptyList()
    private var sessionBestLapMs: Long? = null

    suspend fun onRecordingStarted(sessionId: Long) {
        attachStoredTrackConfig(sessionId)
        resetLive(sessionId)
    }

    suspend fun onRecordingStopped(sessionId: Long) {
        processSession(sessionId)
        _liveState.value = PredictiveTimingState()
        predictiveEngine = null
        prevSample = null
    }

    suspend fun attachStoredTrackConfig(sessionId: Long) {
        val startFinish = settingsPreferences.trackStartFinishGeoJson.first() ?: return
        trackConfigDao.insert(
            TrackConfigEntity(
                sessionId = sessionId,
                name = "Track",
                startFinishGeoJson = startFinish,
                sectorLinesGeoJson = settingsPreferences.trackSectorLinesGeoJson.first(),
            ),
        )
    }

    fun updateLive(snapshot: TelemetrySnapshot, recording: Boolean) {
        if (!recording) return
        val engine = predictiveEngine ?: return
        val sample = snapshot.toTimingSample()
        val prev = prevSample
        _liveState.value = engine.update(sample, prev, bestSplits, sessionBestLapMs)
        prevSample = sample
    }

    suspend fun processSession(sessionId: Long): LapTimingSummary? {
        val config = trackConfigDao.getForSession(sessionId) ?: return null
        val startFinish = parseStartFinishFromGeoJson(config.startFinishGeoJson) ?: return null
        val samples = sampleDao.getBySession(sessionId)
        if (samples.size < 2) return null

        val laps = LapDetector(startFinish).process(samples, sessionId)
        if (laps.isEmpty()) return LapTimingSummary(emptyList(), null, 0L, emptyMap())
        lapDao.insertAll(laps)
        val savedLaps = lapDao.getBySession(sessionId)
        val sectorLines = parseSectorLinesFromGeoJson(config.sectorLinesGeoJson)
        val calculator = SectorSplitCalculator(sectorLines)
        val splitsByLap = mutableMapOf<Long, List<SectorSplitEntity>>()
        val allSplits = mutableListOf<SectorSplitEntity>()
        for (lap in savedLaps) {
            val result = calculator.computeForLap(lap, samples)
            val withIds = result.splits.map { it.copy(lapId = lap.id) }
            splitsByLap[lap.id] = withIds
            allSplits += withIds
        }
        if (allSplits.isNotEmpty()) sectorSplitDao.insertAll(allSplits)
        val sessionBest = lapDao.bestLapMs(sessionId)
        val theoretical = PredictiveTimingEngine.theoreticalBestFromLaps(savedLaps, allSplits)
        return LapTimingSummary(savedLaps, sessionBest, theoretical, splitsByLap)
    }

    suspend fun loadSummary(sessionId: Long): LapTimingSummary? {
        val laps = lapDao.getBySession(sessionId)
        if (laps.isEmpty()) return null
        val splitsByLap = laps.associate { lap ->
            lap.id to sectorSplitDao.getByLap(lap.id)
        }
        val allSplits = splitsByLap.values.flatten()
        return LapTimingSummary(
            laps = laps,
            sessionBestMs = lapDao.bestLapMs(sessionId),
            theoreticalBestMs = PredictiveTimingEngine.theoreticalBestFromLaps(laps, allSplits),
            splitsByLap = splitsByLap,
        )
    }

    suspend fun getTrackConfig(sessionId: Long): TrackConfigEntity? =
        trackConfigDao.getForSession(sessionId)

    fun bestLap(laps: List<LapEntity>): LapEntity? =
        laps.filter { it.isValid && !it.isOutLap }.minByOrNull { it.durationMs }

    private suspend fun resetLive(sessionId: Long) {
        val config = trackConfigDao.getForSession(sessionId)
        val sectorLines = parseSectorLinesFromGeoJson(config?.sectorLinesGeoJson)
        predictiveEngine = PredictiveTimingEngine(sectorLines)
        val samples = sampleDao.getBySession(sessionId)
        val startMs = samples.lastOrNull()?.timestampMs ?: System.currentTimeMillis()
        predictiveEngine?.reset(startMs)
        prevSample = samples.lastOrNull()
        bestSplits = emptyList()
        sessionBestLapMs = lapDao.bestLapMs(sessionId)
    }

    private fun TelemetrySnapshot.toTimingSample() =
        dev.foss.expeditiongauge.data.db.entities.SampleEntity(
            sessionId = 0L,
            timestampMs = timestampMs,
            latitude = latitude,
            longitude = longitude,
            speedMps = speedMps,
            headingDeg = headingDeg,
        )
}
