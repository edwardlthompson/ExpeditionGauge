package dev.foss.expeditiongauge.obd.dtc

/** One stored DTC with catalog description (OBDex CC0 title or fallback). */
data class DtcEntry(
    val code: String,
    val description: String,
    val freezeSummary: String? = null,
)
