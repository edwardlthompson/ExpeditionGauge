package dev.foss.expeditiongauge.playbackspeed

/** Clamp Relive playback speed to 0.25×–4×. */
object PlaybackSpeed {
    const val MIN = 0.25f
    const val MAX = 4f
    const val STEP = 0.25f

    fun clamp(multiplier: Float): Float = multiplier.coerceIn(MIN, MAX)

    fun step(current: Float, delta: Float): Float = clamp(current + delta)
}
