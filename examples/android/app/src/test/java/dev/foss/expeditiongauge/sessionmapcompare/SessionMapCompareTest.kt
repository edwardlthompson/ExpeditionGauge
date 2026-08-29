package dev.foss.expeditiongauge.sessionmapcompare

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionMapCompareTest {
    @Test
    fun dropsSamplesWithoutFix() {
        val left = listOf(
            SampleEntity(sessionId = 1, timestampMs = 1, latitude = 1.0, longitude = 2.0),
            SampleEntity(sessionId = 1, timestampMs = 2),
        )
        val right = listOf(SampleEntity(sessionId = 2, timestampMs = 1, latitude = 3.0, longitude = 4.0))
        val (a, b) = SessionMapCompare.pair(left, right)
        assertEquals(1, a.size)
        assertEquals(3.0, b.single().lat, 0.0)
    }
}
