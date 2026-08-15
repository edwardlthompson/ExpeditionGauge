package dev.foss.expeditiongauge.obd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Elm327ProtocolTest {
    @org.junit.Before
    fun resetFraming() {
        Elm327Protocol.canFraming = null
    }

    @Test
    fun parseRpmFrom410CResponse() {
        val rpm = Elm327Protocol.parseRpm("410C0FA0")
        assertNotNull(rpm)
        assertEquals(1000f, rpm!!, 0.1f)
    }

    @Test
    fun parseRpm_canPrefixEvenNibble() {
        assertEquals(1000f, Elm327Protocol.parseRpm("7E804410C0FA0")!!, 0.1f)
    }

    @Test
    fun parseRpm_tenthsOverscaleDividesByTen() {
        // ((0x8C*256)+0xA0)/4 = 9000 → treat as 10× clone scale
        assertEquals(900f, Elm327Protocol.parseRpm("410C8CA0")!!, 0.1f)
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

    @Test
    fun readUntilPrompt_waitsForPromptBeyond20Chars() {
        // Old implementation capped at 20 chars and skipped when !ready().
        val banner = "ELM327 v1.5\r\nSEARCHING...\r\nOK\r\n>"
        val reader = java.io.BufferedReader(java.io.StringReader(banner))
        val out = Elm327Io.readUntilPrompt(reader, timeoutMs = 1_000L)
        assertNotNull(out)
        assertTrue(out!!.endsWith(">"))
        assertTrue(out.length > 20)
    }
}
