package dev.foss.expeditiongauge.obd.dtc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DtcCatalogTest {
    @Test
    fun loadAsset_describesKnownAndUnknown() {
        val catalog = DtcCatalog.load(RuntimeEnvironment.getApplication())
        assertTrue(catalog.size() > 1000)
        assertEquals(
            "Catalyst System Efficiency Below Threshold (Bank 1)",
            catalog.describe("P0420"),
        )
        assertEquals(DtcCatalog.UNKNOWN, catalog.describe("P9999"))
        assertEquals(catalog.describe("p0420"), catalog.describe("P0420"))
    }

    @Test
    fun of_mapLookup() {
        val catalog = DtcCatalog.of(mapOf("P0300" to "Random Misfire"))
        assertEquals("Random Misfire", catalog.describe("P0300"))
        assertEquals(DtcCatalog.UNKNOWN, catalog.describe("P0000"))
    }
}
