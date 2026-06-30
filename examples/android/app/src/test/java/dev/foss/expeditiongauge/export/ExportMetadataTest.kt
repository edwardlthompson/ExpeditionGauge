package dev.foss.expeditiongauge.export

import dev.foss.expeditiongauge.recording.SessionMetadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ExportMetadataTest {
    @Test
    fun metadataExportJsonIncludesTagsAndPhoto() {
        val json = SessionMetadata(
            notes = "Off-road test",
            driverName = "Alex",
            conditions = "Dry",
            tags = listOf("offroad", "crawl"),
            photoUri = "content://dev.foss.expeditiongauge/sessions/1/photo.jpg",
            vehicleConfig = mapOf("tires" to "35in"),
        ).toExportJson()
        assertEquals("Off-road test", json.getString("notes"))
        assertEquals(2, json.getJSONArray("tags").length())
        assertEquals("offroad", json.getJSONArray("tags").getString(0))
        assertTrue(json.getString("photoUri").contains("photo.jpg"))
        assertEquals("35in", json.getJSONObject("vehicleConfig").getString("tires"))
    }
}
