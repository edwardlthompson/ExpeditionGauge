package dev.foss.expeditiongauge.sparetpms

/** Fifth TPMS slot for a spare or inner dual. */
object SpareTpms {
    const val CORNER = "SPARE"

    fun corners(includeSpare: Boolean): List<String> {
        val base = listOf("FL", "FR", "RL", "RR")
        return if (includeSpare) base + CORNER else base
    }

    fun isSpare(id: String): Boolean = id.equals(CORNER, ignoreCase = true)
}
