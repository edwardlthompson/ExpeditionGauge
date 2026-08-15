package dev.foss.expeditiongauge.obd

import org.junit.Assert.assertEquals
import org.junit.Test

class Elm327DtcParseTest {
    @org.junit.Before
    fun resetFraming() {
        Elm327Protocol.canFraming = null
    }

    @Test
    fun parseStoredDtcs_singleCode() {
        // ISO 9141 3-slot: P0133 = 0x01 0x33 (no CAN count byte)
        assertEquals(listOf("P0133"), Elm327Protocol.parseStoredDtcs("43 01 33 00 00 00 00"))
    }

    @Test
    fun parseStoredDtcs_canCountByte_threeCodes() {
        // ISO 15765: 43 <count=3> P0133 P0420 P0113 — old parser shifted every code
        assertEquals(
            listOf("P0133", "P0420", "P0113"),
            Elm327Protocol.parseStoredDtcs("43 03 01 33 04 20 01 13"),
        )
    }

    @Test
    fun parseStoredDtcs_canCountByte_oneCode() {
        assertEquals(listOf("P0420"), Elm327Protocol.parseStoredDtcs("43 01 04 20"))
    }

    @Test
    fun parseStoredDtcs_multiFrameAndTypes() {
        // P0420=04 20, C0035=0x40 0x35, B0001=0x80 0x01, U0100=0xC1 0x00
        val raw = """
            0: 43 04 20 40 35
            1: 80 01 C1 00
        """.trimIndent()
        assertEquals(
            listOf("P0420", "C0035", "B0001", "U0100"),
            Elm327Protocol.parseStoredDtcs(raw),
        )
    }

    @Test
    fun parseStoredDtcs_noDataAndEmpty() {
        assertEquals(emptyList<String>(), Elm327Protocol.parseStoredDtcs("NO DATA"))
        assertEquals(emptyList<String>(), Elm327Protocol.parseStoredDtcs("43 00 00 00 00"))
        assertEquals(emptyList<String>(), Elm327Protocol.parseStoredDtcs(""))
    }

    @Test
    fun parsePendingDtcs_mode07() {
        assertEquals(listOf("P0133"), Elm327Protocol.parseServiceDtcs("47 01 33 00 00 00 00", "47"))
    }

    @Test
    fun parsePendingDtcs_canCountByte() {
        assertEquals(listOf("P0133"), Elm327Protocol.parseServiceDtcs("47 01 01 33", "47"))
    }

    @Test
    fun canFramingFromDpn() {
        assertEquals(true, Elm327DtcParse.canFramingFromDpn("A6"))
        assertEquals(true, Elm327DtcParse.canFramingFromDpn("6"))
        assertEquals(false, Elm327DtcParse.canFramingFromDpn("3"))
    }

    @Test
    fun decodeDtcBytes_families() {
        assertEquals("P0133", Elm327Protocol.decodeDtcBytes(0x01, 0x33))
        assertEquals("C0035", Elm327Protocol.decodeDtcBytes(0x40, 0x35))
        assertEquals("B0001", Elm327Protocol.decodeDtcBytes(0x80, 0x01))
        assertEquals("U0100", Elm327Protocol.decodeDtcBytes(0xC1, 0x00))
    }
}
