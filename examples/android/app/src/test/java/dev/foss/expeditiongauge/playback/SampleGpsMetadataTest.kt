package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SampleGpsMetadataTest {
    @Test
    fun prefersExternalWhenExtrasSayExternal() {
        val sample = SampleEntity(
            id = 1L,
            sessionId = 1L,
            timestampMs = 0L,
            extrasJson = """{"gpsSource":"external","hdop":0.8,"numSatellites":10,"fixQuality":1}""",
        )
        val meta = SampleGpsMetadata.fromSample(sample)
        assertEquals("external", meta.gpsSource)
        assertEquals(10, meta.numSatellites)
        assertTrue(SampleGpsMetadata.prefersExternal(sample))
    }
}
