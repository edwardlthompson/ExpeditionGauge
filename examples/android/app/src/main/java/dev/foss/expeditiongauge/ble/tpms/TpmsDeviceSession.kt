package dev.foss.expeditiongauge.ble.tpms

import dev.foss.expeditiongauge.ble.ImuPlacement

data class TpmsDeviceSession(
    val macAddress: String,
    val corner: ImuPlacement = ImuPlacement.Unassigned,
    val lastReading: TpmsReading? = null,
    val parserId: String = "br",
    val rssi: Int = 0,
) {
    val stale: Boolean
        get() {
            val seen = lastReading?.timestampMs ?: return true
            return System.currentTimeMillis() - seen > STALE_MS
        }

    companion object {
        const val STALE_MS = 60_000L
    }
}
