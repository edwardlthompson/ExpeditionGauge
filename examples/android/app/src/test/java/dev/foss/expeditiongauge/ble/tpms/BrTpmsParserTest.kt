package dev.foss.expeditiongauge.ble.tpms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BrTpmsParserTest {
    @Test
    fun decodesOmadonFixture() {
        val hex = "281E1401558536"
        val data = hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val parser = BrTpmsParser()
        val reading = parser.parse("BR", data)
        assertNotNull(reading)
        assertEquals(20f, reading!!.tempC, 0.1f)
        val absolutePsi = 34.1f
        val relativePsi = absolutePsi - 14.5f
        val relativeKpa = relativePsi * 6.894757f
        assertEquals(relativeKpa, reading.pressureKpa, 1f)
    }
}
