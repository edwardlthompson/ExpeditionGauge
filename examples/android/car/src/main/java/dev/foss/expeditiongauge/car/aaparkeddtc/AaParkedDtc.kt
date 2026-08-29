package dev.foss.expeditiongauge.car.aaparkeddtc

import dev.foss.expeditiongauge.car.DriveHudRow

/** Parked-only stored-DTC list for the Android Auto detail pane. */
object AaParkedDtc {
    const val MAX_ROWS = 6
    const val TITLE = "Stored DTCs"
    const val NEED_PARK = "Park to read codes"

    fun canOpen(parked: Boolean, count: Int): Boolean = parked && count > 0

    fun rows(codes: List<Pair<String, String>>): List<DriveHudRow> =
        codes.take(MAX_ROWS).map { (code, desc) ->
            DriveHudRow(code, desc.ifBlank { "Stored DTC" })
        }
}
