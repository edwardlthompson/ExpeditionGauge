package dev.foss.expeditiongauge.telemetry

data class ImuStatusEntry(
    val deviceId: String,
    val label: String,
    val placement: String,
    val connected: Boolean,
    val signalQuality: String,
    val rawYawDeg: Float? = null,
    val filteredYawDeg: Float? = null,
    val latG: Float? = null,
)

data class TirePressureReading(
    val psi: Float? = null,
    val tempC: Float? = null,
    val stale: Boolean = false,
)

data class TelemetrySnapshot(
    val timestampMs: Long = 0L,
    val pitchDeg: Float = 0f,
    val rollDeg: Float = 0f,
    val headingDeg: Float = 0f,
    val speedMps: Float = 0f,
    val latG: Float = 0f,
    val lonG: Float = 0f,
    val driftAngleDeg: Float? = null,
    val bodyYawDeg: Float? = null,
    val velocityHeadingDeg: Float? = null,
    val slipRatio: Float? = null,
    val rearSlipRatio: Float? = null,
    val slipSource: String? = null,
    val speedFromObd: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitudeM: Double? = null,
    val gpsFix: Boolean = false,
    val gpsSource: String = "phone",
    val hdop: Float? = null,
    val numSatellites: Int? = null,
    val fixQuality: Int = 0,
    val frontLeftPressure: TirePressureReading = TirePressureReading(),
    val frontRightPressure: TirePressureReading = TirePressureReading(),
    val rearLeftPressure: TirePressureReading = TirePressureReading(),
    val rearRightPressure: TirePressureReading = TirePressureReading(),
    val tpms: TpmsSnapshot? = null,
    val batteryVoltage: Float? = null,
    val rpm: Float? = null,
    val throttlePct: Float? = null,
    val engineLoadPct: Float? = null,
    val obdConnected: Boolean = false,
    val imuStatuses: List<ImuStatusEntry> = emptyList(),
    val fusionSource: String = "phone",
    val chassisTwistDeg: Float? = null,
    val peakAbsPitchDeg: Float = 0f,
    val peakAbsRollDeg: Float = 0f,
    val peakPitchDeg: Float = 0f,
    val peakRollDeg: Float = 0f,
    val recordingActive: Boolean = false,
) {
    companion object {
        fun empty(): TelemetrySnapshot = TelemetrySnapshot()
    }
}
