package dev.foss.expeditiongauge.gps

/** USGS 3DEP Elevation Point Query Service (EPQS) — US territories including Puerto Rico. */
object UsgsEpqsClient {
    private const val INVALID_SENTINEL = -1.0e5

    fun buildUrlMeters(latitude: Double, longitude: Double): String =
        "https://epqs.nationalmap.gov/v1/json?x=$longitude&y=$latitude&units=Meters&wkt=false"

    /**
     * Parses EPQS JSON. [value] may be a number or numeric string.
     * Returns null when missing or USGS no-data sentinel.
     * Pure string parse (no org.json) so unit tests run on JVM.
     */
    fun parseElevationMeters(json: String): Double? {
        val key = "\"value\""
        val keyIdx = json.indexOf(key)
        if (keyIdx < 0) return null
        var i = keyIdx + key.length
        while (i < json.length && (json[i] == ' ' || json[i] == '\t' || json[i] == ':')) i++
        if (i >= json.length) return null
        if (json.startsWith("null", i)) return null
        val meters = if (json[i] == '"') {
            val end = json.indexOf('"', i + 1)
            if (end < 0) return null
            json.substring(i + 1, end).toDoubleOrNull() ?: return null
        } else {
            val end = json.indexOfFirstFrom(i) { ch ->
                ch == ',' || ch == '}' || ch == ' ' || ch == '\n' || ch == '\r'
            }
            json.substring(i, end).toDoubleOrNull() ?: return null
        }
        if (meters <= INVALID_SENTINEL || !meters.isFinite()) return null
        return meters
    }

    private fun String.indexOfFirstFrom(start: Int, predicate: (Char) -> Boolean): Int {
        for (idx in start until length) {
            if (predicate(this[idx])) return idx
        }
        return length
    }
}
