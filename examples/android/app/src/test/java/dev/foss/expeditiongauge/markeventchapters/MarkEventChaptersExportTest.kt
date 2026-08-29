package dev.foss.expeditiongauge.markeventchapters

import dev.foss.expeditiongauge.data.db.entities.SessionEventEntity
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkEventChaptersExportTest {
    @Test
    fun buildsChapterTable() {
        val html = MarkEventChaptersExport.htmlSection(
            listOf(
                SessionEventEntity(
                    sessionId = 1,
                    timestampMs = 1500,
                    eventType = "mark",
                    payloadJson = """{"tag":"apex"}""",
                ),
            ),
        )
        assertTrue(html.contains("Chapters"))
        assertTrue(html.contains("apex"))
        assertTrue(html.contains("1500"))
    }
}
