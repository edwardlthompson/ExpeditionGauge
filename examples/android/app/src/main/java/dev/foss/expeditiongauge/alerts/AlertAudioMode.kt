package dev.foss.expeditiongauge.alerts

enum class AlertAudioMode {
    BEEP,
    TTS,
    ;

    companion object {
        fun fromPref(raw: String?): AlertAudioMode =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: BEEP
    }
}
