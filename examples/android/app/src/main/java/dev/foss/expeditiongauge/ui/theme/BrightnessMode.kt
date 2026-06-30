package dev.foss.expeditiongauge.ui.theme

enum class BrightnessMode {
    Auto,
    Day,
    Night,
}

fun BrightnessMode.next(): BrightnessMode = when (this) {
    BrightnessMode.Auto -> BrightnessMode.Day
    BrightnessMode.Day -> BrightnessMode.Night
    BrightnessMode.Night -> BrightnessMode.Auto
}
