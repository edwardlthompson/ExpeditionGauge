package dev.foss.expeditiongauge.parkedautocaldwell

/** Longer still hold when parked so autocal does not fire in traffic. */
object ParkedAutocalDwell {
    const val MOVING_MS = 2_500L
    const val PARKED_MS = 5_000L

    fun holdMs(parked: Boolean): Long = if (parked) PARKED_MS else MOVING_MS
}
