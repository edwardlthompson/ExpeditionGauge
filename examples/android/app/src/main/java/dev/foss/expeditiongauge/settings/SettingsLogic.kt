package dev.foss.expeditiongauge.settings

object SettingsLogic {
    fun isUpdateCheckEnabled(interval: String): Boolean = interval != "off"

    fun intervalForToggle(enabled: Boolean, current: String): String =
        when {
            !enabled -> "off"
            current == "off" -> "daily"
            else -> current
        }
}
