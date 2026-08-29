package dev.foss.expeditiongauge.tpmstempcomp

/** Compensate TPMS pressure to a reference temperature (ideal gas, K). */
object TpmsTempComp {
    const val REF_C = 20f

    fun toKelvin(celsius: Float): Float = celsius + 273.15f

    fun compensateKpa(pressureKpa: Float, tempC: Float, refC: Float = REF_C): Float {
        val t1 = toKelvin(tempC).coerceAtLeast(1f)
        val t0 = toKelvin(refC).coerceAtLeast(1f)
        return pressureKpa * (t0 / t1)
    }
}
