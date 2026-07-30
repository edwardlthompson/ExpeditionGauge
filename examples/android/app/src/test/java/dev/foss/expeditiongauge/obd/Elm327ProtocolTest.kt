package dev.foss.expeditiongauge.obd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class Elm327ProtocolTest {
    @Test
    fun parseRpmFrom410CResponse() {
        val rpm = Elm327Protocol.parseRpm("410C0FA0")
        assertNotNull(rpm)
        assertEquals(1000f, rpm!!, 0.1f)
    }

    @Test
    fun parseVehicleSpeed_anchored() {
        assertEquals(42f, Elm327Protocol.parseVehicleSpeedKmh("410D2A")!!, 0.01f)
    }

    @Test
    fun parseVehicleSpeed_ignoresTrailingJunk() {
        // Old takeLast(2) would read "93" (147) from a polluted buffer.
        assertEquals(
            0f,
            Elm327Protocol.parseVehicleSpeedKmh("410D00410C1F93")!!,
            0.01f,
        )
    }

    @Test
    fun parseVehicleSpeed_rejectsMissingHeader() {
        assertNull(Elm327Protocol.parseVehicleSpeedKmh("NO DATA"))
        assertNull(Elm327Protocol.parseVehicleSpeedKmh("93"))
    }

    @Test
    fun parseVoltageFrom4142Response() {
        val volts = Elm327Protocol.parseVoltage("41423584")
        assertNotNull(volts)
        assertEquals(13.7f, volts!!, 0.05f)
    }

    @Test
    fun parseStoredDtcs_singleCode() {
        // P0133 = 0x01 0x33
        assertEquals(listOf("P0133"), Elm327Protocol.parseStoredDtcs("43 01 33 00 00 00 00"))
    }

    @Test
    fun parseStoredDtcs_multiFrameAndTypes() {
        // P0420=04 20, C0035=(01<<6)|0x00 + 0x35 → 0x40 0x35, B0001=0x80 0x01, U0100=0xC1 0x00
        val raw = """
            0: 43 04 20 40 35
            1: 80 01 C1 00
        """.trimIndent()
        val codes = Elm327Protocol.parseStoredDtcs(raw)
        assertEquals(listOf("P0420", "C0035", "B0001", "U0100"), codes)
    }

    @Test
    fun parseStoredDtcs_noDataAndEmpty() {
        assertEquals(emptyList<String>(), Elm327Protocol.parseStoredDtcs("NO DATA"))
        assertEquals(emptyList<String>(), Elm327Protocol.parseStoredDtcs("43 00 00 00 00"))
        assertEquals(emptyList<String>(), Elm327Protocol.parseStoredDtcs(""))
    }

    @Test
    fun decodeDtcBytes_families() {
        assertEquals("P0133", Elm327Protocol.decodeDtcBytes(0x01, 0x33))
        assertEquals("C0035", Elm327Protocol.decodeDtcBytes(0x40, 0x35))
        assertEquals("B0001", Elm327Protocol.decodeDtcBytes(0x80, 0x01))
        assertEquals("U0100", Elm327Protocol.decodeDtcBytes(0xC1, 0x00))
    }

    @Test
    fun parseMonitorStatus_milAndCount() {
        // 0x81 = MIL on + 1 code
        val status = ObdMonitorStatus.parse("410181076504")
        assertNotNull(status)
        assertEquals(true, status!!.milOn)
        assertEquals(1, status.storedDtcCount)
    }

    @Test
    fun parseMonitorStatus_zeroCodes() {
        val status = ObdMonitorStatus.parse("410100000000")
        assertNotNull(status)
        assertEquals(false, status!!.milOn)
        assertEquals(0, status.storedDtcCount)
    }
}
