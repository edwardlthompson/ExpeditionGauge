package dev.foss.expeditiongauge.ambient

object AmbientAutodim {
    const val MIN_BRIGHTNESS = 0.18f
    const val MAX_BRIGHTNESS = 0.95f
    const val NIGHT_LUX = 15f
    const val DAY_LUX = 600f

    fun brightness(lux: Float?): Float? {
        if (lux == null || lux < 0f) return null
        val span = DAY_LUX - NIGHT_LUX
        val t = ((lux - NIGHT_LUX) / span).coerceIn(0f, 1f)
        return MIN_BRIGHTNESS + (MAX_BRIGHTNESS - MIN_BRIGHTNESS) * t
    }

    fun night(lux: Float?): Boolean = lux != null && lux < NIGHT_LUX
}
