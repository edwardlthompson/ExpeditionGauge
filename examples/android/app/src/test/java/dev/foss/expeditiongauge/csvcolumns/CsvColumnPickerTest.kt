package dev.foss.expeditiongauge.csvcolumns

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class CsvColumnPickerTest {
    @Test
    fun filterKeepsSelectedColumns() {
        val csv = "timestampMs,lat,lon,speedMps\n1,2,3,4"
        val out = CsvColumnPicker.filterCsv(csv, setOf("timestampMs", "speedMps"))
        assertEquals("timestampMs,speedMps\n1,4", out)
    }

    @Test
    fun toggleAndParseIgnoreUnknown() {
        val next = CsvColumnPicker.toggle(setOf("lat"), "lon")
        assertEquals(setOf("lat", "lon"), next)
        assertFalse("nope" in CsvColumnPicker.parse("lat,nope"))
        assertEquals("lat,lon", CsvColumnPicker.encode(setOf("lon", "lat")))
    }
}
