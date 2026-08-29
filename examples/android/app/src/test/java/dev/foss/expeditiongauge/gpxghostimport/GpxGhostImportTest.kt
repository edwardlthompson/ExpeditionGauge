package dev.foss.expeditiongauge.gpxghostimport

import org.junit.Assert.assertEquals
import org.junit.Test

class GpxGhostImportTest {
    @Test
    fun parsesGpxAndFit() {
        val gpx = GpxGhostImport.parse("""<trkpt lat="1.5" lon="2.5"><time>x</time></trkpt>""")
        assertEquals(1.5, gpx.single().latitude, 0.0)
        val fit = GpxGhostImport.parse("3.0,4.0,1500\n5.0,6.0")
        assertEquals(2, fit.size)
        assertEquals(1500L, GpxGhostImport.toSamples(fit)[0].timestampMs)
    }
}
