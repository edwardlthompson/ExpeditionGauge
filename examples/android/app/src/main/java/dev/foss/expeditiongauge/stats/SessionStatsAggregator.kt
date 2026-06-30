package dev.foss.expeditiongauge.stats

import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity

data class SessionStatsSummary(
    val sessionId: Long,
    val name: String,
    val durationMs: Long,
    val maxBetaDeg: Float?,
    val peakLatG: Float?,
    val slipEventCount: Int,
    val eventCount: Int,
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

    val slipDelta: Int = left.slipEventCount - right.slipEventCount
}

class SessionStatsAggregator {
    fun summarize(
        session: RecordingSessionEntity,
        eventCount: Int,
        peakLatG: Float? = null,
        maxBetaDeg: Float? = null,
        slipEventCount: Int = 0,
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
        )
    }

    fun compare(left: SessionStatsSummary, right: SessionStatsSummary): SessionComparison {
        return SessionComparison(left = left, right = right)
    }
}
