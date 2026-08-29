package dev.foss.expeditiongauge.display

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DisplayModeSelectorTest {
    @Test
    fun picksFastestSameSize() {
        val current = DisplayModeChoice(1, 1080, 2400, 60f)
        val modes = listOf(
            current,
            DisplayModeChoice(2, 1080, 2400, 120f),
            DisplayModeChoice(3, 1440, 3200, 144f),
        )
        assertEquals(120f, DisplayModeSelector.fastestSameResolution(modes, current)?.refreshHz)
    }

    @Test
    fun emptyModesIsNoOp() {
        val current = DisplayModeChoice(1, 1080, 2400, 60f)
        assertNull(DisplayModeSelector.fastestSameResolution(emptyList(), current))
    }
}
