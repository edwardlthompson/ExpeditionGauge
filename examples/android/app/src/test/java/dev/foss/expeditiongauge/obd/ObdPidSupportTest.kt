package dev.foss.expeditiongauge.obd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.io.StringReader

class ObdPidSupportTest {
    @Test
    fun parse0100IncludesThrottlePlate() {
        // BE1FB813: PID 11 (throttle plate) is set in byte C.
        val pids = ObdPidSupport.parseBitmap("4100BE1FB813", "4100", 0x01)
        assertTrue(0x11 in pids)
        assertTrue(0x0C in pids)
        assertTrue(0x10 in pids)
    }

    @Test
    fun parse0140AppDBit() {
        // PID 0x49 is bit 7 of the second bitmap byte.
        val pids = ObdPidSupport.parseBitmap("414000800000", "4140", 0x41)
        assertEquals(setOf(0x49), pids)
    }

    @Test
    fun parseRejectsShortPayload() {
        assertTrue(ObdPidSupport.parseBitmap("414000", "4140", 0x41).isEmpty())
        assertTrue(ObdPidSupport.parseBitmap("NO DATA", "4140", 0x41).isEmpty())
    }
}

class ObdThrottleQueryTest {
    @Test
    fun discoverPrefersAppDWhenBitmapAndValuePresent() {
        val ch = ObdThrottleQuery.discover(elmReader("414000800000\r>", "4149C8\r>"), elmWriter())
        assertEquals("0149", ch.command)
        assertEquals(false, ch.pcmHeader)
    }

    @Test
    fun discoverFallsBackToPlateWhenNoApp() {
        val ch = ObdThrottleQuery.discover(
            elmReader(
                "414080000000\r>",
                "OK\r>",
                "NO DATA\r>",
                "NO DATA\r>",
                "NO DATA\r>",
                "OK\r>",
            ),
            elmWriter(),
        )
        assertEquals("0111", ch.command)
    }

    @Test
    fun readAppDScalesToPercent() {
        val pct = ObdThrottleQuery.read(
            elmReader("4149C8\r>"),
            elmWriter(),
            ObdThrottleQuery.byCommand("0149")!!,
        )
        assertEquals(200f * 100f / 255f, pct!!, 0.05f)
    }

    @Test
    fun readFordMode22UsesHalfScale() {
        val ch = ObdThrottleQuery.byCommand("2209D4")!!
        val pct = ObdThrottleQuery.read(
            elmReader("OK\r>", "6209D464\r>", "OK\r>"),
            elmWriter(),
            ch,
        )
        assertEquals(50f, pct!!, 0.05f)
    }

    private fun elmReader(vararg chunks: String): BufferedReader =
        BufferedReader(StringReader(chunks.joinToString("")))

    private fun elmWriter(): OutputStreamWriter =
        OutputStreamWriter(java.io.ByteArrayOutputStream())
}
