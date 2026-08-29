package dev.foss.expeditiongauge.ghostvideooverlay

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertTrue
import org.junit.Test

class GhostVideoOverlayTest {
    @Test
    fun pairsLapAndGhostOverlayLines() {
        val lap = SampleEntity(sessionId = 1, timestampMs = 1000L, speedMps = 10f, latG = 0.4f)
        val ghost = SampleEntity(sessionId = 1, timestampMs = 1000L, speedMps = 20f, latG = 0.8f)
        val lines = GhostVideoOverlay.pairedLines(lap, ghost)
        assertTrue(lines.first().contains("Lap"))
        assertTrue(lines.any { it.contains("|") && it.contains("latG") })
    }

    @Test
    fun linesForTimestampUsesNearestGhost() {
        val lap = listOf(SampleEntity(sessionId = 1, timestampMs = 2000L, speedMps = 5f))
        val ghost = listOf(
            SampleEntity(sessionId = 2, timestampMs = 1000L, speedMps = 8f),
            SampleEntity(sessionId = 2, timestampMs = 3000L, speedMps = 30f),
        )
        val lines = GhostVideoOverlay.linesForTimestamp(lap, ghost, 2100L)
        assertTrue(lines.any { it.contains("|") })
    }
}
