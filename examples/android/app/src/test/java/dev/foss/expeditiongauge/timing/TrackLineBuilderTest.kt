package dev.foss.expeditiongauge.timing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackLineBuilderTest {
    @Test
    fun buildsGeoJsonForPerpendicularLine() {
        val line = TrackLineBuilder.perpendicularLine(45.0, -122.0, 90f, widthM = 20.0)
        val json = TrackLineBuilder.toStartFinishGeoJson(line)
        assertTrue(json.contains("LineString"))
        assertTrue(json.contains("coordinates"))
    }

    @Test
    fun appendSectorLineAddsFirstSegment() {
        val line = TrackLineBuilder.perpendicularLine(1.0, 2.0, 30f)
        val json = TrackLineBuilder.appendSectorLine(null, line)
        assertEquals(1, TrackLineBuilder.sectorCount(json))
        assertTrue(json.contains("MultiLineString"))
    }
}
