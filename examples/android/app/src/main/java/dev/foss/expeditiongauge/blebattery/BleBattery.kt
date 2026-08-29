package dev.foss.expeditiongauge.blebattery

/** Map a BLE battery byte onto HUD icon bands. */
object BleBattery {
    fun parsePercent(raw: Int?): Int? = raw?.coerceIn(0, 100)

    fun band(pct: Int?): String = when {
        pct == null -> "unknown"
        pct <= 15 -> "low"
        pct <= 35 -> "warn"
        else -> "ok"
    }
}
