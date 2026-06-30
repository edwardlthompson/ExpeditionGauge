package dev.foss.expeditiongauge.obd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class Elm327ProtocolTest {
    @Test
    fun parseRpmFrom410CResponse() {
        val rpm = Elm327Protocol.parseRpm("410C0FA0")
        assertNotNull(rpm)
        assertEquals(1000f, rpm!!, 0.1f)
    }

    @Test
    fun parseSingleByteSpeed() {
        assertEquals(42f, Elm327Protocol.parseSingleByte("410D2A"), 0.01f)
    }

    @Test
    fun parseVoltageFrom4142Response() {
        val volts = Elm327Protocol.parseVoltage("41423584")
        assertNotNull(volts)
        assertEquals(13.7f, volts!!, 0.05f)
    }
}
