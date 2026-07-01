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
        assertFalse(spec.useCompactGps)
    }

    @Test
    fun portraitSpec_usesFullGpsLayout() {
        val spec = OrientationLayoutEngine.spec(widthDp = 400f, heightDp = 800f)
        assertFalse(spec.isLandscape)
        assertEquals(148f, spec.attitudeGaugeSizeDp, 0.01f)
        assertFalse(spec.useCompactGps)
    }

    @Test
    fun squareWindow_treatedAsPortrait() {
        val spec = OrientationLayoutEngine.spec(600f, 600f)
        assertFalse(spec.isLandscape)
    }

    @Test
    fun tallPortrait_usesThreeTileMode() {
        val spec = OrientationLayoutEngine.spec(400f, 800f)
        assertEquals(HudTileMode.THREE_TILE, spec.tileMode)
    }

    @Test
    fun shortPortrait_usesTwoTileFallback() {
        val spec = OrientationLayoutEngine.spec(400f, 400f)
        assertEquals(HudTileMode.TWO_TILE, spec.tileMode)
    }

    @Test
    fun wideLandscape_usesThreeTileMode() {
        val spec = OrientationLayoutEngine.spec(800f, 400f)
        assertEquals(HudTileMode.THREE_TILE, spec.tileMode)
    }

    @Test
    fun narrowLandscape_usesTwoTileFallback() {
        val spec = OrientationLayoutEngine.spec(320f, 400f)
        assertEquals(HudTileMode.TWO_TILE, spec.tileMode)
    }
}
