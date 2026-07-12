package dev.foss.expeditiongauge.gauge

/**
 * Coordinate display formats for HUD GPS readouts.
 */
object CoordinateFormat {
    enum class Mode { DMS, DECIMAL }

    fun formatDms(value: Double, isLatitude: Boolean): String {
        val abs = kotlin.math.abs(value)
        val degrees = abs.toInt()
        val minutes = ((abs - degrees) * 60).toInt()
        val seconds = (abs - degrees - minutes / 60.0) * 3600
        return "$degrees°$minutes'${"%.1f".format(seconds)}\"${hemisphere(value, isLatitude)}"
    }

    fun formatDecimal(value: Double, isLatitude: Boolean): String =
        "${"%.6f".format(kotlin.math.abs(value))}°${hemisphere(value, isLatitude)}"

    fun formatLine(value: Double, isLatitude: Boolean, mode: Mode): String = when (mode) {
        Mode.DMS -> formatDms(value, isLatitude)
        Mode.DECIMAL -> formatDecimal(value, isLatitude)
    }

    fun formatPair(latitude: Double, longitude: Double, mode: Mode): String =
        "${formatLine(latitude, true, mode)}\n${formatLine(longitude, false, mode)}"

    private fun hemisphere(value: Double, isLatitude: Boolean): String = when {
        isLatitude && value >= 0 -> "N"
        isLatitude -> "S"
        value >= 0 -> "E"
        else -> "W"
    }
}
