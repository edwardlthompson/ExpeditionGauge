package dev.foss.expeditiongauge.gearestimate

object GearEstimate {
    const val MIN_RPM = 800f
    const val MIN_SPEED_KMH = 3f

    fun kmhPerThousandRpm(rpm: Float, speedKmh: Float): Float = speedKmh / (rpm / 1_000f)

    fun estimate(rpm: Float?, speedMps: Float?): Int? {
        if (rpm == null || rpm < MIN_RPM) return null
        val kmh = (speedMps ?: return null) * 3.6f
        if (kmh < MIN_SPEED_KMH) return null
        val k = kmhPerThousandRpm(rpm, kmh)
        return when {
            k < 10f -> 1
            k < 16f -> 2
            k < 23f -> 3
            k < 30f -> 4
            k < 38f -> 5
            else -> 6
        }
    }

    fun line(rpm: Float?, speedMps: Float?): String? {
        val gear = estimate(rpm, speedMps) ?: return null
        return "Gear $gear"
    }

    fun matches(line: String): Boolean = line.startsWith("Gear")
}
