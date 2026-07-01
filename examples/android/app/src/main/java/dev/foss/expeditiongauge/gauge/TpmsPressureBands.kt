package dev.foss.expeditiongauge.gauge

/** TPMS pressure band thresholds stored in kPa (SI). */
object TpmsPressureBands {
    const val LOW_KPA = 28f * 6.894757f
    const val CRITICAL_KPA = 25f * 6.894757f

    enum class Band { OK, LOW, CRITICAL, DISCONNECTED }

    fun band(psi: Float?): Band = when {
        psi == null -> Band.DISCONNECTED
        psi * 6.894757f < CRITICAL_KPA -> Band.CRITICAL
        psi * 6.894757f < LOW_KPA -> Band.LOW
        else -> Band.OK
    }

    fun worst(bands: Iterable<Band>): Band = bands.maxByOrNull { it.ordinal } ?: Band.OK
}
