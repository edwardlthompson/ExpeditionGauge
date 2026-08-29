package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TelemetryCubeLayoutTest {
    @Test
    fun sevenEqualRowsFillTheCube() {
        val size = 280
        val slots = TelemetryCubeLayout.compute(size)
        val used = slots.inset * 2f + slots.rowH * TelemetryCubeLayout.ROW_COUNT
        assertEquals(size.toFloat(), used, 0.6f)
        assertEquals(7, TelemetryCubeLayout.ROW_COUNT)
        for (i in 1 until TelemetryCubeLayout.ROW_COUNT) {
            val prev = slots.rowTop(i - 1)
            val next = slots.rowTop(i)
            assertEquals(slots.rowH, next - prev, 0.05f)
        }
        assertTrue("text stays inside its row", slots.textSize < slots.rowH)
        assertTrue("icons stay inside the link row", slots.iconSize < slots.rowH)
        assertTrue("pedal stays inside the last row", slots.pedalH < slots.rowH)
        val pedalTop = slots.rowTop(TelemetryCubeLayout.PEDAL_ROW) +
            (slots.rowH - slots.pedalH) / 2f
        val linkBottom = slots.rowTop(TelemetryCubeLayout.LINK_ROW) + slots.rowH
        assertTrue("link row sits above the pedal row", linkBottom <= pedalTop + 0.5f)
        assertTrue(
            slots.rowTop(TelemetryCubeLayout.PEDAL_ROW) + slots.rowH <=
                size - slots.inset + 0.5f,
        )
    }

    @Test
    fun smallCubeStillHasSevenRowsAndAVisiblePedal() {
        val slots = TelemetryCubeLayout.compute(160)
        assertEquals(
            160f,
            slots.inset * 2f + slots.rowH * TelemetryCubeLayout.ROW_COUNT,
            0.6f,
        )
        assertTrue(slots.pedalH >= 4f)
        assertTrue(slots.iconSize >= 8f)
    }

    @Test
    fun largerTextScaleGrowsGlyphsWithoutLeavingTheRow() {
        val base = TelemetryCubeLayout.compute(280)
        val large = TelemetryCubeLayout.compute(280, textScale = 1.5f)
        assertTrue(large.textSize > base.textSize)
        assertTrue(large.textSize < large.rowH)
        assertTrue(large.iconSize < large.rowH)
    }
}
