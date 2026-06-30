package dev.foss.expeditiongauge.ble

enum class ImuPlacement(val label: String) {
    FrontLeft("FL"),
    FrontRight("FR"),
    RearLeft("RL"),
    RearRight("RR"),
    Unassigned("—"),
    ;

    companion object {
        fun fromLabel(label: String): ImuPlacement =
            entries.firstOrNull { it.label.equals(label, ignoreCase = true) || it.name.equals(label, ignoreCase = true) }
                ?: Unassigned
    }
}
