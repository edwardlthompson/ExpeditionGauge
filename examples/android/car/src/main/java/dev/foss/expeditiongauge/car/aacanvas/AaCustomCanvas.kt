package dev.foss.expeditiongauge.car.aacanvas

object AaCustomCanvas {
    enum class SurfaceState { PENDING, LIVE, FAILED }
    enum class Kind { SURFACE, PANE }

    fun kind(state: SurfaceState): Kind =
        if (state == SurfaceState.FAILED) Kind.PANE else Kind.SURFACE
}
