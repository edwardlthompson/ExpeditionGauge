package dev.foss.expeditiongauge.fdroidreproducible

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FdroidReproducibleTest {
    @Test
    fun requiresListingFilesAndPinnedEpoch() {
        val present = FdroidReproducible.requiredFiles.toSet()
        assertTrue(FdroidReproducible.complete(present))
        assertFalse(FdroidReproducible.complete(present - "license.txt"))
        assertTrue(FdroidReproducible.epochPinned("1700000000"))
        assertFalse(FdroidReproducible.epochPinned(""))
    }
}
