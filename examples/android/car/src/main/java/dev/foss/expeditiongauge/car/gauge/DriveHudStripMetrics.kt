package dev.foss.expeditiongauge.car.gauge

/** ROW strip cube size and permanent DTC footer band. */
object DriveHudStripMetrics {
    const val DEFAULT_CUBE_PX = 280

    /** Permanent ROW DTC band (~18% of cube; readable after Surface scale-to-fit). */
    fun footerPxFor(cubePx: Int): Int = (cubePx * 18 / 100).coerceIn(52, 72)
}
