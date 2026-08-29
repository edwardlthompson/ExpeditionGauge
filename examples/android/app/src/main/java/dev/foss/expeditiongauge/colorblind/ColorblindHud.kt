package dev.foss.expeditiongauge.colorblind

enum class ColorblindHudMode { NONE, DEUTERANOPIA, PROTANOPIA, TRITANOPIA }

object ColorblindHud {
    fun parse(raw: String?): ColorblindHudMode =
        ColorblindHudMode.entries.firstOrNull { it.name == raw } ?: ColorblindHudMode.NONE

    fun cycle(mode: ColorblindHudMode): ColorblindHudMode {
        val all = ColorblindHudMode.entries
        return all[(all.indexOf(mode) + 1) % all.size]
    }

    fun alertRed(mode: ColorblindHudMode): Long = when (mode) {
        ColorblindHudMode.NONE -> 0xFFFF3333
        ColorblindHudMode.DEUTERANOPIA, ColorblindHudMode.PROTANOPIA -> 0xFF0072B2
        ColorblindHudMode.TRITANOPIA -> 0xFFD55E00
    }

    fun alertYellow(mode: ColorblindHudMode): Long = when (mode) {
        ColorblindHudMode.NONE -> 0xFFFFCC00
        else -> 0xFFF0E442
    }

    fun label(mode: ColorblindHudMode): String = when (mode) {
        ColorblindHudMode.NONE -> "Default"
        ColorblindHudMode.DEUTERANOPIA -> "Deuteranopia"
        ColorblindHudMode.PROTANOPIA -> "Protanopia"
        ColorblindHudMode.TRITANOPIA -> "Tritanopia"
    }
}
