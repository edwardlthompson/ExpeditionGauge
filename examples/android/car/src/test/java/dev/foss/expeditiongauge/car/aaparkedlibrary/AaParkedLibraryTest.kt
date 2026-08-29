package dev.foss.expeditiongauge.car.aaparkedlibrary

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AaParkedLibraryTest {
    @Test
    fun opensWhenParkedAndFormatsDurations() {
        assertTrue(AaParkedLibrary.canOpen(true))
        assertFalse(AaParkedLibrary.canOpen(false))
        assertEquals("45s", AaParkedLibrary.durationLabel(1_000L, 46_000L))
        assertEquals("2m 3s", AaParkedLibrary.durationLabel(0L, 123_000L))
        val rows = AaParkedLibrary.rows(
            listOf(
                Triple("Night run", 0L, 45_000L),
                Triple("", 0L, 1_000L),
            ),
        )
        assertEquals("Night run", rows[0].title)
        assertEquals("45s", rows[0].text)
        assertEquals("Session", rows[1].title)
        val many = AaParkedLibrary.rows((1..8).map { Triple("S$it", 0L, 1_000L) })
        assertEquals(AaParkedLibrary.MAX_ROWS, many.size)
    }
}
