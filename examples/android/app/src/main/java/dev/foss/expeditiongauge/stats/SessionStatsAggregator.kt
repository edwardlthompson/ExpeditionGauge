package dev.foss.expeditiongauge.stats

import dev.foss.expeditiongauge.recording.ActivityType
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.timing.LapTimingService

data class SessionStatsSummary(
    val sessionId: Long,
    val name: String,
    val durationMs: Long,
    val maxBetaDeg: Float?,
    val peakLatG: Float?,
    val slipEventCount: Int,
    val eventCount: Int,
    val bestLapMs: Long? = null,
    val sparklineLatG: List<Float> = emptyList(),
    val activityType: ActivityType = ActivityType.DRIVE,
    val routeThumb: List<Pair<Float, Float>> = emptyList(),
    val routeThumbSegments: List<SessionThumbnailGenerator.ColoredSegment> = emptyList(),
)

data class SessionAggregateStats(
    val sessionCount: Int,
    val totalDurationMs: Long,
    val bestLapMs: Long?,
)

data class SessionComparison(
    val left: SessionStatsSummary,
    val right: SessionStatsSummary,
) {
    val peakGDelta: Float?
        get() {
            val l = left.peakLatG ?: return null
            val r = right.peakLatG ?: return null
            return l - r
        }

    val bestLapDeltaMs: Long?
        get() {
            val l = left.bestLapMs ?: return null
            val r = right.bestLapMs ?: return null
            return l - r
        }

    val slipDelta: Int = left.slipEventCount - right.slipEventCount
}

class SessionStatsAggregator(
    private val slipThreshold: Float = 0.15f,
) {
    fun computeFromSamples(samples: List<SampleEntity>): SampleMetrics =
        SampleMetricsCalculator.computeFromSamples(samples, slipThreshold)

    suspend fun summarizeSession(
        database: ExpeditionGaugeDatabase,
        lapTimingService: LapTimingService,
        session: RecordingSessionEntity,
    ): SessionStatsSummary {
        val samples = database.sampleDao().getBySession(session.id)
        val metrics = computeFromSamples(samples)
        val eventCount = database.sessionEventDao().countBySession(session.id)
        val bestLapMs = lapTimingService.loadSummary(session.id)?.sessionBestMs
        val end = session.endTimeMs ?: System.currentTimeMillis()
        val thumb = SessionThumbnailGenerator.generate(samples)
        return SessionStatsSummary(
            sessionId = session.id,
            name = session.name,
            durationMs = (end - session.startTimeMs).coerceAtLeast(0L),
            maxBetaDeg = metrics.maxBetaDeg,
            peakLatG = metrics.peakLatG,
            slipEventCount = metrics.slipEventCount,
            eventCount = eventCount,
            bestLapMs = bestLapMs,
            sparklineLatG = metrics.sparklineLatG,
            activityType = session.activityType,
            routeThumb = thumb.points,
            routeThumbSegments = thumb.segments,
        )
    }

    suspend fun loadSummaries(
        database: ExpeditionGaugeDatabase,
        lapTimingService: LapTimingService,
        sessions: List<RecordingSessionEntity>,
    ): List<SessionStatsSummary> = sessions.map { summarizeSession(database, lapTimingService, it) }

    fun aggregate(summaries: List<SessionStatsSummary>): SessionAggregateStats = SessionAggregateStats(
        sessionCount = summaries.size,
        totalDurationMs = summaries.sumOf { it.durationMs },
        bestLapMs = summaries.mapNotNull { it.bestLapMs }.minOrNull(),
    )

    fun summarize(
        session: RecordingSessionEntity,
        eventCount: Int,
        peakLatG: Float? = null,
        maxBetaDeg: Float? = null,
        slipEventCount: Int = 0,
        bestLapMs: Long? = null,
        sparklineLatG: List<Float> = emptyList(),
        activityType: ActivityType = ActivityType.DRIVE,
        routeThumb: List<Pair<Float, Float>> = emptyList(),
        routeThumbSegments: List<SessionThumbnailGenerator.ColoredSegment> = emptyList(),
    ): SessionStatsSummary {
        val end = session.endTimeMs ?: System.currentTimeMillis()
        return SessionStatsSummary(
            sessionId = session.id,
            name = session.name,
            durationMs = (end - session.startTimeMs).coerceAtLeast(0L),
            maxBetaDeg = maxBetaDeg,
            peakLatG = peakLatG,
            slipEventCount = slipEventCount,
            eventCount = eventCount,
            bestLapMs = bestLapMs,
            sparklineLatG = sparklineLatG,
            activityType = activityType,
            routeThumb = routeThumb,
            routeThumbSegments = routeThumbSegments,
        )
    }

    fun compare(left: SessionStatsSummary, right: SessionStatsSummary): SessionComparison {
        return SessionComparison(left = left, right = right)
    }
}
