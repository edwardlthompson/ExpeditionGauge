package dev.foss.expeditiongauge.trailertpms

/** Trailer / 5th-wheel TPMS axle ids. */
object TrailerTpms {
    val AXLES = listOf("T1L", "T1R", "T2L", "T2R")

    fun allCorners(includeTrailer: Boolean): List<String> {
        val truck = listOf("FL", "FR", "RL", "RR")
        return if (includeTrailer) truck + AXLES else truck
    }

    fun isTrailer(id: String): Boolean = id.startsWith("T") && id.length >= 3
}
