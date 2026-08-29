package dev.foss.expeditiongauge.terraintoggle

import org.junit.Assert.assertEquals
import org.junit.Test

class TerrainToggleTest {
    @Test
    fun swapsHillshadeWhenOn() {
        val base = "https://demotiles.maplibre.org/style.json"
        assertEquals(base, TerrainToggle.styleUrl(base, terrainOn = false))
        assertEquals(TerrainToggle.HILLSHADE_STYLE, TerrainToggle.styleUrl(base, terrainOn = true))
    }
}
