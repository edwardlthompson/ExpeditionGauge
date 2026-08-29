package dev.foss.expeditiongauge.trailertpms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrailerTpmsTest {
    @Test
    fun addsTrailerAxles() {
        assertEquals(4, TrailerTpms.allCorners(false).size)
        assertEquals(8, TrailerTpms.allCorners(true).size)
        assertTrue(TrailerTpms.isTrailer("T1L"))
    }
}
