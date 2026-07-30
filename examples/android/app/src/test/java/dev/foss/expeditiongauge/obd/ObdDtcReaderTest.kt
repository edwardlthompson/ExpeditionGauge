package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.obd.dtc.DtcCatalog
import dev.foss.expeditiongauge.obd.dtc.DtcEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.io.StringReader
import java.nio.charset.StandardCharsets

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ObdDtcReaderTest {
    private val catalog = DtcCatalog.of(mapOf("P0420" to "Catalyst"))

    @Test
    fun refresh_skipsMode03WhenZeroAndEmpty() {
        val bytes = ByteArrayOutputStream()
        val reader = BufferedReader(StringReader("410100000000\r>"))
        val writer = OutputStreamWriter(bytes, StandardCharsets.UTF_8)
        val out = ObdDtcReader.refresh(reader, writer, catalog, emptyList())
        assertTrue(out.isEmpty())
        assertEquals("0101\r", bytes.toString(StandardCharsets.UTF_8))
    }

    @Test
    fun refresh_clearsWhenZeroAndHadCodes() {
        val bytes = ByteArrayOutputStream()
        val reader = BufferedReader(StringReader("410100000000\r>"))
        val writer = OutputStreamWriter(bytes, StandardCharsets.UTF_8)
        val prev = listOf(DtcEntry("P0420", "Catalyst"))
        val out = ObdDtcReader.refresh(reader, writer, catalog, prev)
        assertTrue(out.isEmpty())
        assertEquals("0101\r", bytes.toString(StandardCharsets.UTF_8))
    }
}
