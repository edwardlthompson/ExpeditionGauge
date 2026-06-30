package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionMetadataTest {
    @Test
    fun roundTripsTagsAndVehicleConfig() {
        val meta = SessionMetadata(
            notes = "Test run",
            driverName = "Driver",
            tags = listOf("drift", "wet"),
            vehicleConfig = mapOf("tires" to "225/45"),
        )
        val entity = meta.applyTo(
            RecordingSessionEntity(name = "Session", startTimeMs = 0L),
        )
        val restored = SessionMetadata.fromEntity(entity)
        assertEquals("Test run", restored.notes)
        assertEquals(listOf("drift", "wet"), restored.tags)
        assertEquals("225/45", restored.vehicleConfig["tires"])
    }

    @Test
    fun tagsJsonRoundTrip() {
        val json = SessionMetadata.tagsToJson(listOf("a", "b"))
        assertEquals(listOf("a", "b"), SessionMetadata.parseTags(json))
    }
}
