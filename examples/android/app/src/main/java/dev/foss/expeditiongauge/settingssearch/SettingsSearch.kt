package dev.foss.expeditiongauge.settingssearch

/** Match a settings query to hub categories. */
object SettingsSearch {
    data class Entry(val category: String, val keywords: List<String>)

    val catalog = listOf(
        Entry("Display", listOf("theme", "brightness", "hud", "night")),
        Entry("Recording", listOf("record", "storage", "interval", "csv")),
        Entry("Alerts", listOf("alert", "tts", "haptic", "snooze")),
        Entry("Hardware", listOf("obd", "tpms", "imu", "gps", "ble")),
        Entry("Maps", listOf("map", "offline", "tile", "style")),
        Entry("Advanced", listOf("privacy", "backup", "live", "developer", "qr")),
    )

    fun match(query: String): List<String> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return catalog.map { it.category }
        return catalog.filter { entry ->
            entry.category.lowercase().contains(q) ||
                entry.keywords.any { it.contains(q) || q.contains(it) }
        }.map { it.category }
    }
}
