package dev.foss.expeditiongauge.sessionsplitmerge

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionSplitMergeTest {
    @Test
    fun splitAtKeepsOrderAndMidpoint() {
        val samples = listOf(sample(1, 1000), sample(2, 2000), sample(3, 3000))
        val (left, right) = SessionSplitMerge.splitAt(samples, 2000)
        assertEquals(listOf(1000L), left.map { it.timestampMs })
        assertEquals(listOf(2000L, 3000L), right.map { it.timestampMs })
        assertEquals(2000L, SessionSplitMerge.midpointMs(samples))
    }

    @Test
    fun mergeSortsAndRemaps() {
        val merged = SessionSplitMerge.merge(
            listOf(sample(1, 3000)),
            listOf(sample(2, 1000)),
        )
        assertEquals(listOf(1000L, 3000L), merged.map { it.timestampMs })
        val remapped = SessionSplitMerge.remap(merged, 9L)
        assertTrue(remapped.all { it.id == 0L && it.sessionId == 9L })
        assertEquals("A (2)", SessionSplitMerge.splitName("A", 2))
        assertEquals("A + B", SessionSplitMerge.mergeName("A", "B"))
    }

    private fun sample(id: Long, ts: Long) = SampleEntity(
        id = id,
        sessionId = 1L,
        timestampMs = ts,
    )
}
