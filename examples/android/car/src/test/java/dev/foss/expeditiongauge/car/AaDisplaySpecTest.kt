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
        assertEquals(360, spec.bitmapSizePx)
        assertFalse(spec.isUltraWide)
    }

    @Test
    fun portraitUsesSmallerAttitudeDp() {
        val spec = AaDisplaySpec.from(widthDp = 400, heightDp = 800, density = 2f)
        assertFalse(spec.isLandscape)
        assertEquals(AaDisplaySpec.PORTRAIT_ATTITUDE_DP, spec.attitudeSizeDp, 0.01f)
        assertEquals(296, spec.bitmapSizePx)
    }

    @Test
    fun ultraWideDetectedAtRatioTwo() {
        val spec = AaDisplaySpec.from(widthDp = 1000, heightDp = 400, density = 1.5f)
        assertTrue(spec.isLandscape)
        assertTrue(spec.isUltraWide)
    }

    @Test
    fun mixedOrientationContract_vehicleAxesIndependentOfHuAspect() {
        // Same vehicle-frame P/R must drive tiles regardless of HU portrait/landscape.
        // Spec only changes size fields — never remaps attitude.
        val portraitHu = AaDisplaySpec.from(400, 800, 2f)
        val landscapeHu = AaDisplaySpec.from(800, 400, 2f)
        assertTrue(portraitHu.bitmapSizePx != landscapeHu.bitmapSizePx)
        assertEquals(portraitHu.isDarkMode, landscapeHu.isDarkMode)
    }

    @Test
    fun gridLimitCoercedAtLeastOne() {
        val spec = AaDisplaySpec.from(800, 400, 2f, maxGridItems = 0)
        assertEquals(1, spec.maxGridItems)
    }
}
