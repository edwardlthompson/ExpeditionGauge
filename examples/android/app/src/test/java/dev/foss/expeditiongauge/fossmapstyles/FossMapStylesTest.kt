package dev.foss.expeditiongauge.fossmapstyles

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FossMapStylesTest {
    @Test
    fun cyclesAndAppliesTerrain() {
        val next = FossMapStyles.cycle("demo")
        assertEquals("liberty", next.id)
        assertTrue(FossMapStyles.url("liberty", terrainOn = true).contains("fiord"))
        assertEquals(FossMapStyles.byId("bright").url, FossMapStyles.url("bright", terrainOn = false))
    }
}
