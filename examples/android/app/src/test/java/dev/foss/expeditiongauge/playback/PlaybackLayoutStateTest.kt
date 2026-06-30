package dev.foss.expeditiongauge.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackLayoutStateTest {
    @Test
    fun withMapWeightClampsAndBalancesGauges() {
        val layout = PlaybackLayoutState().withMapWeight(0.9f)
        assertEquals(0.8f, layout.mapWeight, 0.001f)
        assertEquals(0.2f, layout.gaugesWeight, 0.001f)
    }

    @Test
    fun withMapWeightRejectsTooSmall() {
        val layout = PlaybackLayoutState().withMapWeight(0.1f)
        assertEquals(0.2f, layout.mapWeight, 0.001f)
        assertTrue(layout.gaugesWeight > 0.7f)
    }
}
