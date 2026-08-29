package dev.foss.expeditiongauge.nmealogexport

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NmeaLogExportTest {
    @Test
    fun retainsOnlyNmeaAndEvictsOld() {
        NmeaLogExport.clear()
        NmeaLogExport.retain("not nmea")
        NmeaLogExport.retain("\$GPGGA,1")
        NmeaLogExport.retain("\$GPRMC,2", maxLines = 1)
        assertEquals(listOf("\$GPRMC,2"), NmeaLogExport.snapshot())
        assertTrue(NmeaLogExport.toFileText().contains("GPRMC"))
        NmeaLogExport.clear()
    }
}
