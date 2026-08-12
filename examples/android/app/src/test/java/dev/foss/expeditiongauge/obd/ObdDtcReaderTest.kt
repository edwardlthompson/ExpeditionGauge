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
    fun refresh_readsMode03And07When0101Zero() {
        val bytes = ByteArrayOutputStream()
        val reader = BufferedReader(
            StringReader("410100000000\r>NO DATA\r>NO DATA\r>"),
        )
        val writer = OutputStreamWriter(bytes, StandardCharsets.UTF_8)
        val out = ObdDtcReader.refresh(reader, writer, catalog, emptyList())
        assertTrue(out.isEmpty())
        val sent = bytes.toString(StandardCharsets.UTF_8)
        assertTrue(sent.contains("0101\r"))
        assertTrue(sent.contains("03\r"))
        assertTrue(sent.contains("07\r"))
    }

    @Test
    fun refresh_keepsPreviousWhenCountPositiveButReadsEmpty() {
        val bytes = ByteArrayOutputStream()
        val reader = BufferedReader(
            StringReader("410181000000\r>NO DATA\r>NO DATA\r>"),
        )
        val writer = OutputStreamWriter(bytes, StandardCharsets.UTF_8)
        val prev = listOf(DtcEntry("P0420", "Catalyst"))
        val out = ObdDtcReader.refresh(reader, writer, catalog, prev)
        assertEquals(prev, out)
    }

    @Test
    fun mergeDistinct_pendingFillsGaps() {
        val stored = listOf(DtcEntry("P0420", "Catalyst"))
        val pending = listOf(
            DtcEntry("P0420", "Pending — Catalyst"),
            DtcEntry("P0300", "Pending — Random misfire"),
        )
        val out = ObdDtcReader.mergeDistinct(stored, pending)
        assertEquals(listOf("P0420", "P0300"), out.map { it.code })
    }
}
