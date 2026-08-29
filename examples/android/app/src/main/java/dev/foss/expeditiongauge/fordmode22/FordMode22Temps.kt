package dev.foss.expeditiongauge.fordmode22

data class FordMode22Temps(
    val transC: Float? = null,
    val egtC: Float? = null,
)

object FordMode22TempLine {
    fun line(temps: FordMode22Temps?): String? {
        if (temps == null) return null
        val parts = buildList {
            temps.transC?.let { add("TFT ${it.toInt()}°C") }
            temps.egtC?.let { add("EGT ${it.toInt()}°C") }
        }
        return parts.joinToString(" · ").ifBlank { null }
    }
}
