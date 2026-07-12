package dev.foss.expeditiongauge.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoordinateFormatTest {
    @Test
    fun formatDms_knownPoint() {
        // 18.457361 N ≈ 18°27'26.5"N (matches prior HUD DMS)
        val lat = 18.0 + 27.0 / 60.0 + 26.5 / 3600.0
        val lon = -(66.0 + 11.0 / 60.0 + 4.0 / 3600.0)
        assertEquals("18°27'26.5\"N", CoordinateFormat.formatDms(lat, true))
        assertEquals("66°11'4.0\"W", CoordinateFormat.formatDms(lon, false))
    }

    @Test
    fun formatDecimal_sixPlaces() {
        val lat = 18.457250
        val lon = -66.184583
        assertEquals("18.457250°N", CoordinateFormat.formatDecimal(lat, true))
        assertEquals("66.184583°W", CoordinateFormat.formatDecimal(lon, false))
    }

    @Test
    fun formatPair_joinsLines() {
        val pair = CoordinateFormat.formatPair(18.5, -66.2, CoordinateFormat.Mode.DECIMAL)
        assertTrue(pair.contains("\n"))
        assertEquals(
            "18.500000°N\n66.200000°W",
            pair,
        )
    }

    @Test
    fun modeToggle_switchesFormatter() {
        val value = 10.5
        val dms = CoordinateFormat.formatLine(value, true, CoordinateFormat.Mode.DMS)
        val dec = CoordinateFormat.formatLine(value, true, CoordinateFormat.Mode.DECIMAL)
        assertEquals("10°30'0.0\"N", dms)
        assertEquals("10.500000°N", dec)
    }
}
