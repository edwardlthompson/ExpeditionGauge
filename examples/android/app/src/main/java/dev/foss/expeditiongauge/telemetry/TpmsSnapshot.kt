package dev.foss.expeditiongauge.telemetry

data class TpmsCornerReading(
    val pressureKpa: Float? = null,
    val tempC: Float? = null,
    val batteryPct: Int? = null,
    val lastSeenMs: Long = 0L,
)

data class TpmsSnapshot(
    val frontLeft: TpmsCornerReading = TpmsCornerReading(),
    val frontRight: TpmsCornerReading = TpmsCornerReading(),
    val rearLeft: TpmsCornerReading = TpmsCornerReading(),
    val rearRight: TpmsCornerReading = TpmsCornerReading(),
) {
    fun corner(placement: dev.foss.expeditiongauge.ble.ImuPlacement): TpmsCornerReading = when (placement) {
        dev.foss.expeditiongauge.ble.ImuPlacement.FrontLeft -> frontLeft
        dev.foss.expeditiongauge.ble.ImuPlacement.FrontRight -> frontRight
        dev.foss.expeditiongauge.ble.ImuPlacement.RearLeft -> rearLeft
        dev.foss.expeditiongauge.ble.ImuPlacement.RearRight -> rearRight
        dev.foss.expeditiongauge.ble.ImuPlacement.Unassigned -> TpmsCornerReading()
    }

    fun withCorner(placement: dev.foss.expeditiongauge.ble.ImuPlacement, reading: TpmsCornerReading): TpmsSnapshot =
        when (placement) {
            dev.foss.expeditiongauge.ble.ImuPlacement.FrontLeft -> copy(frontLeft = reading)
            dev.foss.expeditiongauge.ble.ImuPlacement.FrontRight -> copy(frontRight = reading)
            dev.foss.expeditiongauge.ble.ImuPlacement.RearLeft -> copy(rearLeft = reading)
            dev.foss.expeditiongauge.ble.ImuPlacement.RearRight -> copy(rearRight = reading)
            dev.foss.expeditiongauge.ble.ImuPlacement.Unassigned -> this
        }
}
