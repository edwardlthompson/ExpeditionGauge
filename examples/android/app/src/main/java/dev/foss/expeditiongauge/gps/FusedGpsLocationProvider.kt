package dev.foss.expeditiongauge.gps

import android.content.Context
import dev.foss.expeditiongauge.drift.DriftAngleEstimator
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
    val headingDeg: Float? = null,
    val hdop: Float? = null,
    val numSatellites: Int? = null,
    val fixQuality: Int = 0,
)

/** Prefers external NMEA GPS when connected and valid; falls back to phone GPS. */
class FusedGpsLocationProvider(
    context: Context,
    private val telemetryBus: TelemetryBus,
    private val driftEstimator: DriftAngleEstimator,
    private val externalGps: ExternalNmeaGpsManager,
    private val demElevation: DemElevationLookup,
) {
    private val phoneGps = PhoneGpsProvider(context, driftEstimator, demElevation) { onPhoneSnapshot(it) }
    private val _state = MutableStateFlow(FusedGpsState())
    val state: StateFlow<FusedGpsState> = _state.asStateFlow()

    fun startPhone() = phoneGps.start()
    fun stopPhone() = phoneGps.stop()

    fun onExternalFix(fix: NmeaFix) {
        if (!fix.valid || ExternalNmeaGpsManager.isStale(fix)) {
            // Keep EXTERNAL while the link is up; only drop to phone when external is gone.
            if (_state.value.source == GpsSource.EXTERNAL && !externalGpsActive()) {
                _state.value = _state.value.copy(source = GpsSource.PHONE)
            }
            return
        }
        val heading = fix.courseDeg ?: _state.value.headingDeg
        val speed = fix.speedMps ?: 0f
        if (heading != null) driftEstimator.onGpsSample(heading, speed)
        val state = ExternalFixAltitude.toState(fix, heading, speed, demElevation)
        _state.value = state
        publishExternal(state)
        ExternalFixAltitude.refreshDemIfNeeded(fix, demElevation) { demMeters ->
            val current = _state.value
            if (current.source != GpsSource.EXTERNAL) return@refreshDemIfNeeded
            val updated = current.copy(altitudeM = demMeters)
            _state.value = updated
            publishExternal(updated)
        }
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
            headingDeg = snapshot.velocityHeadingDeg,
            hdop = snapshot.hdop,
            numSatellites = snapshot.numSatellites,
            fixQuality = snapshot.fixQuality,
        )
        publishPhone(snapshot)
    }

    private fun externalGpsActive(): Boolean =
        GpsSourcePriority.preferExternal(
            externalConnected = externalGps.connected,
            externalFix = externalGps.fix.value,
        )

    private fun publishPhone(snapshot: TelemetrySnapshot) {
        val merged = GpsHeadingMerge.withCourse(
            current = telemetryBus.snapshots.value,
            speedMps = snapshot.speedMps,
            gpsCourseDeg = snapshot.velocityHeadingDeg,
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
    }

    private fun publishExternal(gps: FusedGpsState) {
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
    }
}
