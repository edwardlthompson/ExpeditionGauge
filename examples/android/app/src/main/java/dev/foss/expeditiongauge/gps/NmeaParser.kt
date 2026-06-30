package dev.foss.expeditiongauge.gps

data class NmeaFix(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitudeM: Double? = null,
    val speedMps: Float? = null,
    val courseDeg: Float? = null,
    val hdop: Float? = null,
    val numSatellites: Int? = null,
    val fixQuality: Int = 0,
    val valid: Boolean = false,
    val timestampMs: Long = System.currentTimeMillis(),
)

/**
 * Parses common NMEA sentences (GGA, RMC, VTG, GSA).
 */
object NmeaParser {
    fun parseLine(line: String): NmeaFix? {
        val trimmed = line.trim()
        if (!trimmed.startsWith("$")) return null
        val body = trimmed.removePrefix("$").substringBefore("*")
        val parts = body.split(",")
        if (parts.isEmpty()) return null
        val type = parts[0].takeLast(3)
        return when (type) {
            "GGA" -> parseGga(parts)
            "RMC" -> parseRmc(parts)
            "VTG" -> parseVtg(parts)
            "GSA" -> parseGsa(parts)
            else -> null
        }
    }

    fun parseBuffer(text: String): NmeaFix {
        var merged = NmeaFix()
        text.lines().forEach { line ->
            val fix = parseLine(line) ?: return@forEach
            merged = merged.merge(fix)
        }
        return merged
    }

    private fun parseGga(parts: List<String>): NmeaFix? {
        if (parts.size < 10) return null
        val quality = parts[6].toIntOrNull() ?: 0
        val sats = parts[7].toIntOrNull()
        val hdop = parts[8].toFloatOrNull()
        val alt = parts[9].toDoubleOrNull()
        val lat = parseLatLon(parts[2], parts[3], parts[4], parts[5]) ?: return NmeaFix(
            fixQuality = quality,
            numSatellites = sats,
            hdop = hdop,
            altitudeM = alt,
            valid = quality > 0,
        )
        return NmeaFix(
            latitude = lat.first,
            longitude = lat.second,
            altitudeM = alt,
            fixQuality = quality,
            numSatellites = sats,
            hdop = hdop,
            valid = quality > 0,
        )
    }

    private fun parseRmc(parts: List<String>): NmeaFix? {
        if (parts.size < 10) return null
        val status = parts[2]
        if (status != "A") return NmeaFix(valid = false)
        val lat = parseLatLon(parts[3], parts[4], parts[5], parts[6]) ?: return null
        val speedKnots = parts[7].toFloatOrNull() ?: 0f
        val course = parts[8].toFloatOrNull()
        return NmeaFix(
            latitude = lat.first,
            longitude = lat.second,
            speedMps = speedKnots * KNOTS_TO_MPS,
            courseDeg = course,
            valid = true,
            fixQuality = 1,
        )
    }

    private fun parseVtg(parts: List<String>): NmeaFix? {
        if (parts.size < 8) return null
        val course = parts[1].toFloatOrNull()
        val speedKmh = parts[7].toFloatOrNull() ?: parts[5].toFloatOrNull()
        return NmeaFix(
            courseDeg = course,
            speedMps = (speedKmh ?: 0f) / 3.6f,
            valid = course != null,
        )
    }

    private fun parseGsa(parts: List<String>): NmeaFix? {
        if (parts.size < 18) return null
        return NmeaFix(
            hdop = parts[16].toFloatOrNull(),
            valid = true,
        )
    }

    private fun parseLatLon(latStr: String, latHem: String, lonStr: String, lonHem: String): Pair<Double, Double>? {
        if (latStr.isBlank() || lonStr.isBlank()) return null
        val lat = nmeaToDecimal(latStr, latHem == "S") ?: return null
        val lon = nmeaToDecimal(lonStr, lonHem == "W") ?: return null
        return lat to lon
    }

    private fun nmeaToDecimal(value: String, negative: Boolean): Double? {
        val dot = value.indexOf('.')
        if (dot < 3) return null
        val degrees = value.substring(0, dot - 2).toDoubleOrNull() ?: return null
        val minutes = value.substring(dot - 2).toDoubleOrNull() ?: return null
        val decimal = degrees + minutes / 60.0
        return if (negative) -decimal else decimal
    }

    private fun NmeaFix.merge(other: NmeaFix): NmeaFix = copy(
        latitude = other.latitude ?: latitude,
        longitude = other.longitude ?: longitude,
        altitudeM = other.altitudeM ?: altitudeM,
        speedMps = other.speedMps ?: speedMps,
        courseDeg = other.courseDeg ?: courseDeg,
        hdop = other.hdop ?: hdop,
        numSatellites = other.numSatellites ?: numSatellites,
        fixQuality = if (other.fixQuality > 0) other.fixQuality else fixQuality,
        valid = other.valid || valid,
        timestampMs = other.timestampMs,
    )

    private const val KNOTS_TO_MPS = 0.514444f
}
