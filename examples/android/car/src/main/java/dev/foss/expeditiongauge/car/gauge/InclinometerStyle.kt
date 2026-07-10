package dev.foss.expeditiongauge.car.gauge

/**
 * Visual styles for pitch/roll. Always vehicle-frame: pitch → vertical cue,
 * roll → lateral cue (no G-meter screen remap).
 */
enum class InclinometerStyle {
    /** Center pitch ladder + L/R communicating-vessel roll (offroad dash). */
    LADDER,

    /** Artificial horizon: sky/ground banked by roll, pitch ladder. */
    HORIZON,

    /** Twin circular dials — pitch left, roll right. */
    DUAL_DIAL,

    /** Dual spirit-level tubes — pitch vertical, roll horizontal. */
    BUBBLE,
}

fun InclinometerStyle.next(): InclinometerStyle {
    val all = InclinometerStyle.entries
    return all[(ordinal + 1) % all.size]
}

fun InclinometerStyle.storageKey(): String = name.lowercase()

fun inclinometerStyleFromStorage(raw: String?): InclinometerStyle =
    InclinometerStyle.entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
        ?: InclinometerStyle.LADDER
