package dev.foss.expeditiongauge.car

import org.junit.Assert.assertEquals
import org.junit.Test

class HudStripOrientationTest {
    @Test
    fun emptyRect_defaultsRow() {
        assertEquals(HudStripOrientation.ROW, HudStripOrientation.stable(0, 800, HudStripOrientation.COLUMN))
        assertEquals(HudStripOrientation.ROW, HudStripOrientation.stable(400, 0, HudStripOrientation.COLUMN))
        assertEquals(HudStripOrientation.ROW, HudStripOrientation.stable(-1, -1, HudStripOrientation.ROW))
    }

    @Test
    fun enterColumn_requiresClearTallAspect() {
        // 480x800 → ratio 1.667 > 1.15
        assertEquals(
            HudStripOrientation.COLUMN,
            HudStripOrientation.stable(480, 800, HudStripOrientation.ROW),
        )
        // Just under enter threshold: h = w * 1.15 → stay ROW
        assertEquals(
            HudStripOrientation.ROW,
            HudStripOrientation.stable(100, 115, HudStripOrientation.ROW),
        )
        // Just over
        assertEquals(
            HudStripOrientation.COLUMN,
            HudStripOrientation.stable(100, 116, HudStripOrientation.ROW),
        )
    }

    @Test
    fun leaveColumn_hysteresis() {
        // Already COLUMN; ratio 1.0 (square) still >= 0.95 → stay COLUMN
        assertEquals(
            HudStripOrientation.COLUMN,
            HudStripOrientation.stable(100, 100, HudStripOrientation.COLUMN),
        )
        // Drop below leave threshold
        assertEquals(
            HudStripOrientation.ROW,
            HudStripOrientation.stable(100, 94, HudStripOrientation.COLUMN),
        )
    }

    @Test
    fun cubeCount_rowThree_columnTwo() {
        assertEquals(3, HudStripOrientation.cubeCount(HudStripOrientation.ROW))
        assertEquals(2, HudStripOrientation.cubeCount(HudStripOrientation.COLUMN))
    }
}
