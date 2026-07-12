package dev.foss.expeditiongauge.gps

/**
 * Chooses GPS MSL vs terrain DEM when GNSS vertical quality is weak.
 * A-GPS improves fix/sats but does not supply elevation — DEM fills that gap.
 */
object AltitudeSourcePolicy {
    const val MIN_SATS_FOR_GPS_ALT = 6
    const val MAX_VERTICAL_ACCURACY_M = 15f

    /** Prefer USGS/DEM when sats are few or vertical accuracy is poor. */
    fun preferDemElevation(numSatellites: Int?, verticalAccuracyM: Float?): Boolean {
        if (verticalAccuracyM != null && verticalAccuracyM > MAX_VERTICAL_ACCURACY_M) return true
        if (numSatellites != null && numSatellites < MIN_SATS_FOR_GPS_ALT) return true
        // Missing quality metadata → do not trust phone MSL alone (often tens of meters off).
        if (numSatellites == null && verticalAccuracyM == null) return true
        return false
    }

    fun resolveMeters(gpsMslM: Double, demM: Double?, preferDem: Boolean): Double =
        if (preferDem && demM != null) demM else gpsMslM
}
