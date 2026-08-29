package dev.foss.expeditiongauge.offlinegeocoder

import org.junit.Assert.assertEquals
import org.junit.Test

class OfflineGeocoderTest {
    @Test
    fun titlesFromNearestPlace() {
        assertEquals("Pacific Raceways", OfflineGeocoder.titleFor(47.321, -122.145))
        assertEquals("Session", OfflineGeocoder.titleFor(10.0, 10.0))
        assertEquals("Untitled", OfflineGeocoder.titleFor(null, null, "Untitled"))
    }
}
