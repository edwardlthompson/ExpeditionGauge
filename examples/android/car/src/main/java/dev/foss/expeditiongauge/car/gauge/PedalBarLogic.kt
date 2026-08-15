package dev.foss.expeditiongauge.car.gauge

/** Throttle/brake bar: −1 full brake, 0 rest, +1 full throttle. */
data class PedalBarState(
    val position: Float,
    val throttle01: Float,
    val brake01: Float,
    val flashThrottle: Boolean,
    val flashBrake: Boolean,
)

object PedalBarLogic {
    const val BRAKE_FULL_G = 0.85f
    const val THROTTLE_DEAD = 0.03f
    const val BRAKE_DEAD = 0.04f
    const val FLASH_AT = 0.97f

    fun from(throttlePct: Float?, lonG: Float): PedalBarState {
        val th = ((throttlePct ?: 0f) / 100f).coerceIn(0f, 1f)
        val thAdj = if (th < THROTTLE_DEAD) 0f else th
        val brRaw = if (thAdj > 0f) 0f else (-lonG / BRAKE_FULL_G).coerceIn(0f, 1f)
        val brAdj = if (brRaw < BRAKE_DEAD) 0f else brRaw
        val pos = when {
            thAdj > 0f -> thAdj
            brAdj > 0f -> -brAdj
            else -> 0f
        }
        return PedalBarState(
            position = pos,
            throttle01 = thAdj,
            brake01 = brAdj,
            flashThrottle = thAdj >= FLASH_AT,
            flashBrake = brAdj >= FLASH_AT,
        )
    }

    fun quantize(state: PedalBarState): Int = (state.position * 25f).toInt()
}
