package dev.foss.expeditiongauge.recording

import org.junit.Assert.assertEquals
import org.junit.Test

class SessionMetadataRepositoryTest {
    @Test
    fun saveRoundTripsThroughMetadata() {
        val entity = dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity(
            id = 1L,
            name = "Test",
            startTimeMs = 0L,
        )
        val meta = SessionMetadata(notes = "note", tags = listOf("trail"))
        val updated = meta.applyTo(entity)
        assertEquals("note", updated.notes)
        assertEquals("""["trail"]""", updated.tagsJson)
        assertEquals(listOf("trail"), SessionMetadata.fromEntity(updated).tags)
    }
}
