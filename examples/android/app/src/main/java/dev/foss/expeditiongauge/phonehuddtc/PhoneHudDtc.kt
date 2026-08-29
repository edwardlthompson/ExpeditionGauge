package dev.foss.expeditiongauge.phonehuddtc

import dev.foss.expeditiongauge.obd.dtc.DtcCarousel
import dev.foss.expeditiongauge.obd.dtc.DtcEntry

object PhoneHudDtc {
    fun line(entries: List<DtcEntry>, nowMs: Long): String? =
        DtcCarousel.frame(entries, nowMs)?.line()

    fun current(entries: List<DtcEntry>, nowMs: Long): DtcEntry? {
        val frame = DtcCarousel.frame(entries, nowMs) ?: return null
        return entries.firstOrNull { it.code == frame.code }
    }

    fun fullTitle(entry: DtcEntry): String =
        (entry.code + " " + entry.description).trim()
}
