package dev.foss.expeditiongauge.ble

import dev.foss.expeditiongauge.fusion.ImuOrientationFilter

data class ImuDeviceSession(
    val deviceId: String,
    val displayName: String,
    val placement: ImuPlacement = ImuPlacement.Unassigned,
    val rssi: Int = 0,
    val lastSeenMs: Long = 0L,
    val connected: Boolean = false,
    val filter: ImuOrientationFilter = ImuOrientationFilter(),
) {
    val signalQuality: SignalQuality
        get() = when {
            !connected -> SignalQuality.Disconnected
            rssi >= -60 -> SignalQuality.Good
            rssi >= -75 -> SignalQuality.Fair
            else -> SignalQuality.Poor
        }
}

enum class SignalQuality {
    Good,
    Fair,
    Poor,
    Disconnected,
}
