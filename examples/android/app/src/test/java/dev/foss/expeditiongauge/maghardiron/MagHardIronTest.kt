package dev.foss.expeditiongauge.maghardiron

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MagHardIronTest {
    @Test
    fun fitsOffsetFromSweep() {
        val samples = (0 until 8).map { MagSample(it.toFloat(), it * 2f, 10f) }
        val offset = MagHardIron.fit(samples)
        assertNotNull(offset)
        assertEquals(3.5f, offset!!.x)
        val corrected = MagHardIron.apply(MagSample(3.5f, 7f, 10f), offset)
        assertEquals(0f, corrected.x)
        assertNull(MagHardIron.fit(samples.take(3)))
    }
}
