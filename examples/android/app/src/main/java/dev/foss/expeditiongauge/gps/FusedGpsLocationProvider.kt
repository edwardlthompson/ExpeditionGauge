package dev.foss.expeditiongauge.gps

import android.content.Context
import dev.foss.expeditiongauge.drift.DriftAngleEstimator
import dev.foss.expeditiongauge.sensors.PhoneSensorProvider
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class GpsSource { PHONE, EXTERNAL }

data class FusedGpsState(
    val source: GpsSource = GpsSource.PHONE,
    val fix: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitudeM: Double? = null,
    val speedMps: Float = 0f,
    val headingDeg: Float = 0f,
    val hdop: Float? = null,
    val numSatellites: Int? = null,
    val fixQuality: Int = 0,
)

/** Prefers external NMEA GPS when connected and valid; falls back to phone GPS. */
class FusedGpsLocationProvider(
    context: Context,
    private val telemetryBus: TelemetryBus,
    private val driftEstimator: DriftAngleEstimator,
    private val sensorProvider: PhoneSensorProvider,
    private val externalGps: ExternalNmeaGpsManager,
) {
    private val phoneGps = PhoneGpsProvider(context, driftEstimator) { onPhoneSnapshot(it) }
    private val _state = MutableStateFlow(FusedGpsState())
    val state: StateFlow<FusedGpsState> = _state.asStateFlow()

    fun startPhone() = phoneGps.start()
    fun stopPhone() = phoneGps.stop()

    fun onExternalFix(fix: NmeaFix) {
        if (!fix.valid || ExternalNmeaGpsManager.isStale(fix)) {
            publishPhoneFallback()
            return
        }
        val heading = fix.courseDeg ?: _state.value.headingDeg
        val speed = fix.speedMps ?: 0f
        driftEstimator.onGpsSample(heading, speed)
        val state = FusedGpsState(
            source = GpsSource.EXTERNAL,
            fix = true,
            latitude = fix.latitude,
            longitude = fix.longitude,
            altitudeM = fix.altitudeM,
            speedMps = speed,
            headingDeg = heading,
            hdop = fix.hdop,
            numSatellites = fix.numSatellites,
            fixQuality = fix.fixQuality,
        )
        _state.value = state
        mergeIntoBus(state)
    }

    fun onPhoneSnapshot(snapshot: TelemetrySnapshot) {
        if (externalGpsActive()) return
        _state.value = FusedGpsState(
            source = GpsSource.PHONE,
            fix = snapshot.gpsFix,
            latitude = snapshot.latitude,
            longitude = snapshot.longitude,
            altitudeM = snapshot.altitudeM,
            speedMps = snapshot.speedMps,
            headingDeg = snapshot.headingDeg,
            hdop = snapshot.hdop,
            numSatellites = snapshot.numSatellites,
            fixQuality = snapshot.fixQuality,
        )
        mergePhoneSnapshot(snapshot)
    }

    private fun publishPhoneFallback() {
        if (_state.value.source == GpsSource.EXTERNAL) {
            _state.value = _state.value.copy(source = GpsSource.PHONE)
        }
    }

    private fun externalGpsActive(): Boolean {
        val fix = externalGps.fix.value
        return fix.valid && !ExternalNmeaGpsManager.isStale(fix)
    }

    private fun mergePhoneSnapshot(snapshot: TelemetrySnapshot) {
        val course = snapshot.velocityHeadingDeg ?: snapshot.headingDeg
        val merged = GpsHeadingMerge.withCourse(
            current = telemetryBus.snapshots.value,
            speedMps = snapshot.speedMps,
            gpsCourseDeg = course,
        ) {
            copy(
                timestampMs = snapshot.timestampMs,
                latitude = snapshot.latitude,
                longitude = snapshot.longitude,
                altitudeM = snapshot.altitudeM,
                gpsFix = snapshot.gpsFix,
                gpsSource = "phone",
                hdop = snapshot.hdop,
                numSatellites = snapshot.numSatellites,
                fixQuality = snapshot.fixQuality,
                driftAngleDeg = snapshot.driftAngleDeg,
            )
        }
        telemetryBus.publish(merged)
        sensorProvider.updateGpsContext(merged)
    }

    private fun mergeIntoBus(gps: FusedGpsState) {
        val merged = GpsHeadingMerge.withCourse(
            current = telemetryBus.snapshots.value,
            speedMps = gps.speedMps,
            gpsCourseDeg = gps.headingDeg,
        ) {
            copy(
                timestampMs = System.currentTimeMillis(),
                latitude = gps.latitude,
                longitude = gps.longitude,
                altitudeM = gps.altitudeM,
                gpsFix = gps.fix,
                gpsSource = gps.source.name.lowercase(),
                hdop = gps.hdop,
                numSatellites = gps.numSatellites,
                fixQuality = gps.fixQuality,
                driftAngleDeg = driftEstimator.currentSample().driftAngleDeg,
            )
        }
        telemetryBus.publish(merged)
        sensorProvider.updateGpsContext(merged)
    }
}
