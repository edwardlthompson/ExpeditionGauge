package dev.foss.expeditiongauge.car.aaa11y

object AaA11yType {
    const val LARGE_PREF_SCALE = 1.25f
    const val MIN = 1f
    const val MAX = 1.5f

    fun scale(fontScale: Float, largeTextPref: Boolean = false): Float {
        val raw = if (largeTextPref) fontScale * LARGE_PREF_SCALE else fontScale
        return raw.coerceIn(MIN, MAX)
    }

    fun spoken(label: String, value: String): String {
        val trimmed = value.trim()
        return if (trimmed.isEmpty() || trimmed == "\u200B") label else "$label, $trimmed"
    }
}
