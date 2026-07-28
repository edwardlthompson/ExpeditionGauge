package dev.foss.expeditiongauge.telemetry

/** Illuminated vs gray link indicators for HUD / AA telemetry cube. */
data class SensorLinkState(
    val gpsLinked: Boolean = false,
    val obdLinked: Boolean = false,
    val tpmsLinked: Boolean = false,
    val imuLinked: Boolean = false,
    val gpsSource: String = "phone",
) {
    companion object {
        fun from(snapshot: TelemetrySnapshot): SensorLinkState {
            val tpmsLinked = listOf(
                snapshot.frontLeftPressure,
                snapshot.frontRightPressure,
                snapshot.rearLeftPressure,
                snapshot.rearRightPressure,
            ).any { reading -> reading.psi != null && !reading.stale }
            return SensorLinkState(
                gpsLinked = snapshot.gpsFix,
                obdLinked = snapshot.obdConnected,
                tpmsLinked = tpmsLinked,
                imuLinked = snapshot.imuStatuses.any { it.connected },
                gpsSource = snapshot.gpsSource,
            )
        }
    }
}
