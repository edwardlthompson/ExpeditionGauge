package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TelemetryCubeLayoutTest {
    @Test
    fun fiveLinesFitAbovePedalOnTypicalCube() {
        val size = 280
        val lines = 5
        val slots = TelemetryCubeLayout.compute(size, lines)
        val textH = TelemetryCubeLayout.textBlockH(
            lines, slots.primarySize, slots.secondarySize, slots.gap,
        )
        val textBottom = slots.inset + textH
        assertTrue("lat/lon must sit above the link row", textBottom <= slots.linkY - slots.gap)
        assertEquals(
            size * TelemetryCubeLayout.PEDAL_H_FRAC,
            slots.pedalH,
            0.5f,
        )
        assertEquals(
            size * TelemetryCubeLayout.PRIMARY_FRAC,
            slots.primarySize,
            0.5f,
        )
        assertEquals(
            size * TelemetryCubeLayout.SECONDARY_FRAC,
            slots.secondarySize,
            0.5f,
        )
        assertEquals(
            slots.secondarySize * TelemetryCubeLayout.LINK_H_OVER_SECONDARY,
            slots.linkH,
            0.5f,
        )
        assertTrue("pedal sits under the link icons", slots.linkY + slots.linkH <= slots.pedalY)
        assertTrue(slots.pedalY + slots.pedalH <= size - slots.inset + 0.5f)
    }

    @Test
    fun smallCubeStillKeepsFiveLines() {
        val slots = TelemetryCubeLayout.compute(160, 5)
        val textH = TelemetryCubeLayout.textBlockH(
            5, slots.primarySize, slots.secondarySize, slots.gap,
        )
        assertTrue(slots.inset + textH <= slots.linkY + 1f)
        assertTrue(slots.pedalH >= 3f)
    }

    @Test
    fun zeroLinesStillPlacesPedalAtBottom() {
        val size = 200
        val slots = TelemetryCubeLayout.compute(size, 0)
        assertEquals(
            0f,
            TelemetryCubeLayout.textBlockH(0, slots.primarySize, slots.secondarySize, slots.gap),
        )
        assertTrue(slots.pedalY + slots.pedalH <= size - slots.inset + 0.5f)
        assertTrue(slots.linkY + slots.linkH <= slots.pedalY + 0.5f)
    }
}
