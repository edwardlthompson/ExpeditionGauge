package dev.foss.expeditiongauge.ui.settings

enum class SettingsCategory {
    Display,
    Recording,
    Alerts,
    Hardware,
    Maps,
    Advanced,
}

enum class SettingsBackTarget {
    Hub,
    Exit,
}

fun settingsBackTarget(category: SettingsCategory?): SettingsBackTarget =
    if (category == null) SettingsBackTarget.Exit else SettingsBackTarget.Hub
