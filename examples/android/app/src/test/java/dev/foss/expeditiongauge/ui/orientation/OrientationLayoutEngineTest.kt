package dev.foss.expeditiongauge.ui.orientation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OrientationLayoutEngineTest {

    @Test
    fun landscapeSpec_usesLargeGauges() {
        val spec = OrientationLayoutEngine.spec(widthDp = 800f, heightDp = 400f)
        assertTrue(spec.isLandscape)
        assertEquals(180f, spec.attitudeGaugeSizeDp, 0.01f)
        assertEquals(160f, spec.speedometerGaugeSizeDp, 0.01f)
        assertFalse(spec.useCompactGps)
    }

    @Test
    fun portraitSpec_usesFullGpsLayout() {
        val spec = OrientationLayoutEngine.spec(widthDp = 400f, heightDp = 800f)
        assertFalse(spec.isLandscape)
        assertEquals(148f, spec.attitudeGaugeSizeDp, 0.01f)
        assertEquals(120f, spec.speedometerGaugeSizeDp, 0.01f)
        assertFalse(spec.useCompactGps)
    }

    @Test
    fun squareWindow_treatedAsPortrait() {
        val spec = OrientationLayoutEngine.spec(600f, 600f)
        assertFalse(spec.isLandscape)
    }
}
