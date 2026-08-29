package dev.foss.expeditiongauge.fordmode22

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FordMode22CatalogTest {
    @Test
    fun throttleCommandsMatchExistingProbes() {
        val cmds = FordMode22Catalog.byKind(FordMode22Kind.THROTTLE).map { it.command }
        assertTrue(cmds.containsAll(listOf("2209D4", "220911", "221340")))
    }

    @Test
    fun parseAppAndTransTemp() {
        val app = FordMode22Catalog.byCommand("2209D4")!!
        assertEquals(50f, FordMode22Catalog.parse("62 09 D4 64", app)!!, 0.05f)
        val tft = FordMode22Catalog.byCommand("221E1C")!!
        assertEquals(50f, FordMode22Catalog.parse("62 1E 1C 5A", tft)!!, 0.05f)
        assertNull(FordMode22Catalog.parse("NO DATA", tft))
        assertNull(FordMode22Catalog.byCommand("22FFFF"))
    }

    @Test
    fun summaryListsCommandAndLabel() {
        assertTrue(FordMode22Catalog.summary().contains("2209D4 APP"))
        assertTrue(FordMode22Catalog.summary().contains("221E1C TFT"))
    }
}
