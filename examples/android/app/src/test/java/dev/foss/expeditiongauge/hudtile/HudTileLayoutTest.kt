package dev.foss.expeditiongauge.hudtile

import org.junit.Assert.assertEquals
import org.junit.Test

class HudTileLayoutTest {
    @Test
    fun parseEncodeAndCycle() {
        assertEquals(HudTileLayout.DEFAULT, HudTileLayout.parse(null))
        val swapped = listOf(HudTileId.TELEMETRY, HudTileId.TPMS, HudTileId.ATTITUDE)
        assertEquals(swapped, HudTileLayout.parse("TELEMETRY,TPMS,ATTITUDE"))
        assertEquals("TELEMETRY,TPMS,ATTITUDE", HudTileLayout.encode(swapped))
        assertEquals(
            listOf(HudTileId.TELEMETRY, HudTileId.TPMS, HudTileId.ATTITUDE),
            HudTileLayout.cycle(HudTileLayout.DEFAULT),
        )
    }

    @Test
    fun arrangeFollowsOrderAndFillsGaps() {
        val available = mapOf(
            HudTileId.ATTITUDE to "A",
            HudTileId.TELEMETRY to "T",
        )
        assertEquals(listOf("T", "A"), HudTileLayout.arrange(HudTileLayout.cycle(HudTileLayout.DEFAULT), available))
        assertEquals("ATTITUDE · TELEMETRY · TPMS", HudTileLayout.summary(HudTileLayout.DEFAULT))
    }
}
