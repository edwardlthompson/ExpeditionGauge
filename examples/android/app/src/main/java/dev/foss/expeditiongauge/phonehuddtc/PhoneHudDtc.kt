package dev.foss.expeditiongauge.phonehuddtc

import dev.foss.expeditiongauge.obd.dtc.DtcCarousel
import dev.foss.expeditiongauge.obd.dtc.DtcEntry

object PhoneHudDtc {
    fun line(entries: List<DtcEntry>, nowMs: Long): String? =
        DtcCarousel.frame(entries, nowMs)?.line()
}
