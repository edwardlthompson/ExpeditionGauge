package dev.foss.expeditiongauge.ble.tpms

import org.junit.Assert.assertEquals
import org.junit.Test

class TpmsQrPayloadParserTest {
    @Test
    fun empty_isEmpty() {
        assertEquals(
            TpmsQrParseResult.Invalid(TpmsQrParseResult.Reason.Empty),
            TpmsQrPayloadParser.parse("  "),
        )
    }

    @Test
    fun colonMac_ok() {
        val r = TpmsQrPayloadParser.parse("AA:BB:CC:DD:EE:FF")
        assertEquals(TpmsQrParseResult.Ok("AA:BB:CC:DD:EE:FF"), r)
    }

    @Test
    fun lowercaseCompact_ok() {
        val r = TpmsQrPayloadParser.parse("aabbccddeeff")
        assertEquals(TpmsQrParseResult.Ok("AA:BB:CC:DD:EE:FF"), r)
    }

    @Test
    fun dashMac_ok() {
        val r = TpmsQrPayloadParser.parse("aa-bb-cc-dd-ee-ff")
        assertEquals(TpmsQrParseResult.Ok("AA:BB:CC:DD:EE:FF"), r)
    }

    @Test
    fun macQuery_ok() {
        val r = TpmsQrPayloadParser.parse("https://example.com/pair?mac=AA:BB:CC:DD:EE:FF&x=1")
        assertEquals(TpmsQrParseResult.Ok("AA:BB:CC:DD:EE:FF"), r)
    }

    @Test
    fun urlWithoutMac_noMac() {
        val r = TpmsQrPayloadParser.parse("https://example.com/product/12345")
        assertEquals(TpmsQrParseResult.Invalid(TpmsQrParseResult.Reason.NoMac), r)
    }

    @Test
    fun elevenHex_badLength() {
        val r = TpmsQrPayloadParser.parse("AABBCCDDEEF")
        assertEquals(TpmsQrParseResult.Invalid(TpmsQrParseResult.Reason.BadLength), r)
    }

    @Test
    fun fourHex_sensorId() {
        assertEquals(TpmsQrParseResult.SensorId("A1B2"), TpmsQrPayloadParser.parse("A1B2"))
    }

    @Test
    fun sixHex_sensorId() {
        assertEquals(TpmsQrParseResult.SensorId("002333"), TpmsQrPayloadParser.parse("002333"))
    }

    @Test
    fun eightHex_sensorId() {
        assertEquals(TpmsQrParseResult.SensorId("85AABBCC"), TpmsQrPayloadParser.parse("85aabbcc"))
    }
}
