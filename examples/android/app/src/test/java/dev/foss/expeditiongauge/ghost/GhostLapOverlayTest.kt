package dev.foss.expeditiongauge.ghost

import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GhostLapOverlayTest {
    @Test
    fun sameTrackAllowsGhostCompare() {
        val geo = """{"coordinates":[[0.0,0.0],[0.001,0.0]]}"""
        val primary = listOf(
            SampleEntity(id = 1, sessionId = 1L, timestampMs = 0),
            SampleEntity(id = 2, sessionId = 1L, timestampMs = 1000),
        )
        val ghost = listOf(
            SampleEntity(id = 3, sessionId = 2L, timestampMs = 0),
            SampleEntity(id = 4, sessionId = 2L, timestampMs = 900),
        )
        val state = GhostLapOverlay().buildState(primary, ghost, geo, geo)
        assertFalse(state.trackMismatch)
        assertNotNull(state.deltaMsAtIndex)
    }

    @Test
    fun filtersSamplesForLapByIndex() {
        val samples = (1L..5L).map { id ->
            SampleEntity(id = id, sessionId = 1L, timestampMs = id * 100)
        }
        val lap = LapEntity(sessionId = 1L, lapNumber = 1, startSampleId = 2, endSampleId = 4, durationMs = 200)
        val filtered = GhostLapOverlay().samplesForLap(samples, lap)
        assertEquals(3, filtered.size)
    }

    @Test
    fun detectsTrackMismatch() {
        val near = """{"coordinates":[[0.0,0.0],[0.001,0.0]]}"""
        val far = """{"coordinates":[[0.1,0.0],[0.101,0.0]]}"""
        val state = GhostLapOverlay().buildState(
            primary = listOf(SampleEntity(id = 1, sessionId = 1L, timestampMs = 0)),
            ghost = listOf(SampleEntity(id = 2, sessionId = 2L, timestampMs = 0)),
            primaryStartFinishGeoJson = near,
            ghostStartFinishGeoJson = far,
        )
        assertTrue(state.trackMismatch)
        assertTrue(state.ghostSamples.isEmpty())
    }
}
