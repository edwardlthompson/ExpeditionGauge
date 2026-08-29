package dev.foss.expeditiongauge.gpxbeta

import org.junit.Assert.assertTrue
import org.junit.Test

class GpxBetaExtensionsTest {
    @Test
    fun writesNamespacedLatLonBeta() {
        val xml = GpxBetaExtensions.tags(latG = 0.4f, lonG = -0.2f, betaDeg = 12f)
        assertTrue(xml.contains("eg:latG"))
        assertTrue(xml.contains("eg:lonG"))
        assertTrue(xml.contains("eg:betaDeg"))
        assertTrue(GpxBetaExtensions.xmlnsAttr().contains(GpxBetaExtensions.NS))
    }
}
