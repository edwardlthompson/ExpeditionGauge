package dev.foss.expeditiongauge.fdroidantifeatures

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FdroidAntiFeaturesTest {
    @Test
    fun declaresNone() {
        assertEquals("None", FdroidAntiFeatures.listing())
        assertFalse(FdroidAntiFeatures.applies("Tracking"))
        assertFalse(FdroidAntiFeatures.applies("Ads"))
        assertFalse(FdroidAntiFeatures.applies("NonFreeNet"))
    }
}
