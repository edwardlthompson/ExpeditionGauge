package dev.foss.expeditiongauge.export

import org.junit.Assert.assertTrue
import org.junit.Test

class ExportExtrasParserTest {
    @Test
    fun parsesTpmsCornersFromExtrasJson() {
        val json = """{"tpms":{"fl":{"pressureKpa":220.0,"tempC":25.0},"fr":{"pressureKpa":221.0}}}"""
        val columns = ExportExtrasParser.tpmsColumns(json)
        assertTrue(columns.hasAnyData)
        assertTrue(columns.frontLeft.pressureKpa == 220f)
        assertTrue(columns.frontLeft.tempC == 25f)
        assertTrue(columns.frontRight.pressureKpa == 221f)
    }
}
