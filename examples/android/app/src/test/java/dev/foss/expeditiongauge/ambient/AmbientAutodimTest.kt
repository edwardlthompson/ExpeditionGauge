package dev.foss.expeditiongauge.ambient

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AmbientAutodimTest {
    @Test
    fun mapsLuxToBrightness() {
        assertNull(AmbientAutodim.brightness(null))
        assertEquals(AmbientAutodim.MIN_BRIGHTNESS, AmbientAutodim.brightness(0f)!!, 0.001f)
        assertEquals(AmbientAutodim.MAX_BRIGHTNESS, AmbientAutodim.brightness(800f)!!, 0.001f)
        val mid = AmbientAutodim.brightness(307.5f)!!
        assertTrue(mid > AmbientAutodim.MIN_BRIGHTNESS && mid < AmbientAutodim.MAX_BRIGHTNESS)
        assertTrue(AmbientAutodim.night(5f))
        assertFalse(AmbientAutodim.night(80f))
    }
}
