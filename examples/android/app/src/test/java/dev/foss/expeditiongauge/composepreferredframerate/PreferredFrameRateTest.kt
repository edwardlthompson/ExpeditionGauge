package dev.foss.expeditiongauge.composepreferredframerate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferredFrameRateTest {
    @Test
    fun votesHighOnApi35() {
        assertEquals(120f, PreferredFrameRate.vote(35), 0.01f)
        assertEquals(60f, PreferredFrameRate.vote(34), 0.01f)
        assertTrue(PreferredFrameRate.isHighVote(35))
        assertFalse(PreferredFrameRate.isHighVote(26))
    }
}
