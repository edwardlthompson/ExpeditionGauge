package dev.foss.expeditiongauge.car.aacanvas

import org.junit.Assert.assertEquals
import org.junit.Test

class AaCustomCanvasTest {
    @Test
    fun usesPaneOnlyAfterSurfaceAttachFails() {
        assertEquals(AaCustomCanvas.Kind.SURFACE, AaCustomCanvas.kind(AaCustomCanvas.SurfaceState.PENDING))
        assertEquals(AaCustomCanvas.Kind.SURFACE, AaCustomCanvas.kind(AaCustomCanvas.SurfaceState.LIVE))
        assertEquals(AaCustomCanvas.Kind.PANE, AaCustomCanvas.kind(AaCustomCanvas.SurfaceState.FAILED))
    }
}
