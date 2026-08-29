package dev.foss.expeditiongauge.sessionsplitmerge

import dev.foss.expeditiongauge.data.db.entities.SampleEntity

/** Split samples at a timestamp and merge two sample lists. */
object SessionSplitMerge {
    fun splitAt(
        samples: List<SampleEntity>,
        atMs: Long,
    ): Pair<List<SampleEntity>, List<SampleEntity>> {
        val left = samples.filter { it.timestampMs < atMs }
        val right = samples.filter { it.timestampMs >= atMs }
        return left to right
    }

    fun merge(a: List<SampleEntity>, b: List<SampleEntity>): List<SampleEntity> =
        (a + b).sortedBy { it.timestampMs }

    fun remap(samples: List<SampleEntity>, sessionId: Long): List<SampleEntity> =
        samples.map { it.copy(id = 0L, sessionId = sessionId) }

    fun splitName(base: String, part: Int): String = "$base ($part)"

    fun mergeName(first: String, second: String): String = "$first + $second"

    fun midpointMs(samples: List<SampleEntity>): Long? {
        if (samples.size < 2) return null
        val first = samples.first().timestampMs
        val last = samples.last().timestampMs
        return (first + last) / 2L
    }
}
