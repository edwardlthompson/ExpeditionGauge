package dev.foss.expeditiongauge.car

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AaDisplaySpecTest {
    @Test
    fun landscapeUsesLargerAttitudeDp() {
        val spec = AaDisplaySpec.from(widthDp = 700, heightDp = 400, density = 2f)
        assertTrue(spec.isLandscape)
        assertEquals(AaDisplaySpec.LANDSCAPE_ATTITUDE_DP, spec.attitudeSizeDp, 0.01f)
        assertEquals(AaDisplaySpec.MAX_PANE_BITMAP_PX, spec.paneBitmapSizePx)
        assertFalse(spec.isUltraWide)
    }

    @Test
    fun portraitUsesSmallerAttitudeDp() {
        val spec = AaDisplaySpec.from(widthDp = 400, heightDp = 800, density = 2f)
        assertFalse(spec.isLandscape)
        assertEquals(AaDisplaySpec.PORTRAIT_ATTITUDE_DP, spec.attitudeSizeDp, 0.01f)
        assertEquals(AaDisplaySpec.MAX_PANE_BITMAP_PX, spec.paneBitmapSizePx)
    }

    @Test
    fun ultraWideDetectedAtRatioTwo() {
        val spec = AaDisplaySpec.from(widthDp = 1000, heightDp = 400, density = 1.5f)
        assertTrue(spec.isLandscape)
        assertTrue(spec.isUltraWide)
    }

    @Test
    fun paneBitmapIsThreeCubesWide() {
        val low = AaDisplaySpec.from(800, 400, density = 1f)
        assertEquals(160, low.cubeSizePx)
        assertEquals(480, low.paneWidthPx)
        assertEquals(160, low.paneHeightPx) // native 3×1 strip
        val hi = AaDisplaySpec.from(800, 400, density = 3f)
        assertEquals(AaDisplaySpec.MAX_CUBE_PX, hi.cubeSizePx)
        assertEquals(AaDisplaySpec.MAX_PANE_BITMAP_PX, hi.paneBitmapSizePx)
        assertEquals(hi.cubeSizePx * AaDisplaySpec.CUBE_COUNT, hi.paneWidthPx)
    }

    @Test
    fun gridLimitCoercedAtLeastOne() {
        val spec = AaDisplaySpec.from(800, 400, 2f, maxGridItems = 0)
        assertEquals(1, spec.maxGridItems)
    }

    @Test
    fun surfaceCubePrefersHeightSupersampleAndFloor() {
        // 400h → 600 preferred (1.5×), within cap
        assertEquals(600, AaDisplaySpec.surfaceCubePx(800, 400))
        // Tiny visible area still floors at Pane max (never below 320)
        assertEquals(AaDisplaySpec.MAX_CUBE_PX, AaDisplaySpec.surfaceCubePx(200, 100))
        // Huge HU capped
        assertEquals(AaDisplaySpec.MAX_SURFACE_CUBE_PX, AaDisplaySpec.surfaceCubePx(3000, 900))
    }
}
