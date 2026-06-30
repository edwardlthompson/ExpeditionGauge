package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackEngineTest {
    @Test
    fun seekClampsToValidRange() {
        val engine = PlaybackEngine()
        val samples = (0..9).map { i ->
            SampleEntity(id = i.toLong(), sessionId = 1L, timestampMs = i * 100L)
        }
        engine.loadSession(1L, samples)
        engine.seekToIndex(99)
        assertEquals(9, engine.state.value.currentIndex)
    }

    @Test
    fun computeMarkersFindsHighBeta() {
        val samples = listOf(
            SampleEntity(id = 1, sessionId = 1L, timestampMs = 0, driftAngleDeg = 5f),
            SampleEntity(id = 2, sessionId = 1L, timestampMs = 100, driftAngleDeg = 20f),
        )
        val markers = PlaybackEngine.computeMarkers(samples, betaThreshold = 15f)
        assertTrue(markers.any { it.type == ScrubberMarkerType.HIGH_BETA })
    }

    @Test
    fun applyLayoutUpdatesWeights() {
        val engine = PlaybackEngine()
        engine.applyLayout(PlaybackLayoutState(mapWeight = 0.7f, graphsExpanded = false))
        assertEquals(0.7f, engine.state.value.mapWeight)
        assertEquals(false, engine.state.value.graphsExpanded)
    }
}
