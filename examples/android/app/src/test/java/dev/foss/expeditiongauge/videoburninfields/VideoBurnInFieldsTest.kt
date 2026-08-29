package dev.foss.expeditiongauge.videoburninfields

import org.junit.Assert.assertEquals
import org.junit.Test

class VideoBurnInFieldsTest {
    @Test
    fun pickKeepsEnabledOrder() {
        val lines = mapOf("speed" to "Speed 40", "beta" to "β 12", "latG" to "latG 0.4")
        assertEquals(listOf("Speed 40", "latG 0.4"), VideoBurnInFields.pick(setOf("speed", "latG"), lines))
        assertEquals("beta,latG", VideoBurnInFields.encode(setOf("latG", "beta")))
    }
}
