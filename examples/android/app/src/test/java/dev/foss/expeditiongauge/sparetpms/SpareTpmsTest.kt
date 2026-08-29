package dev.foss.expeditiongauge.sparetpms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpareTpmsTest {
    @Test
    fun includesFifthCorner() {
        assertEquals(4, SpareTpms.corners(false).size)
        assertEquals(5, SpareTpms.corners(true).size)
        assertTrue(SpareTpms.isSpare("spare"))
    }
}
