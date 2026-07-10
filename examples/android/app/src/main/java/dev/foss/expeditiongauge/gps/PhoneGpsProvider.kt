package dev.foss.expeditiongauge.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import dev.foss.expeditiongauge.drift.DriftAngleEstimator
import dev.foss.expeditiongauge.sensors.SensorPollScheduler
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

class PhoneGpsProvider(
    private val context: Context,
    private val driftEstimator: DriftAngleEstimator,
    private val onPhoneFix: (TelemetrySnapshot) -> Unit,
) : LocationListener {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var lastLocation: Location? = null

    fun start() {
        if (!hasLocationPermission()) return
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> return
        }
        try {
            locationManager.requestLocationUpdates(
                provider,
                SensorPollScheduler.PHONE_GPS_INTERVAL_MS,
                0f,
                this,
            )
            locationManager.getLastKnownLocation(provider)?.let { onLocationChanged(it) }
        } catch (_: SecurityException) {
            // Permission not granted at runtime
        }
    }

    fun stop() {
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        val prev = lastLocation
        lastLocation = location
        val speedMps = if (location.hasSpeed()) location.speed else 0f
        val course = resolveCourse(prev, location, speedMps)
        driftEstimator.onGpsSample(course, speedMps)
        val snapshot = TelemetrySnapshot(
            timestampMs = System.currentTimeMillis(),
            speedMps = speedMps,
            headingDeg = course,
            velocityHeadingDeg = course,
            latitude = location.latitude,
            longitude = location.longitude,
            altitudeM = AltitudeNormalizer.fromLocation(location),
            gpsFix = true,
            gpsSource = "phone",
            driftAngleDeg = driftEstimator.currentSample().driftAngleDeg,
        )
        onPhoneFix(snapshot)
    }

    override fun onProviderDisabled(provider: String) = Unit
    override fun onProviderEnabled(provider: String) = Unit
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

    private fun resolveCourse(prev: Location?, location: Location, speedMps: Float): Float {
        if (prev != null) {
            val segmentM = GpsCourseLogic.distanceM(
                prev.latitude, prev.longitude, location.latitude, location.longitude,
            )
            val fromMove = GpsCourseLogic.bearingDeg(
                prev.latitude, prev.longitude, location.latitude, location.longitude,
            )
            if (GpsCourseLogic.isReliableCourse(speedMps, segmentM)) return fromMove
            if (segmentM >= GpsCourseLogic.MIN_SEGMENT_M) return fromMove
        }
        if (location.hasBearing() && speedMps >= GpsCourseLogic.MIN_SPEED_MPS) {
            return GpsCourseLogic.normalize360(location.bearing)
        }
        return if (location.hasBearing()) GpsCourseLogic.normalize360(location.bearing) else 0f
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }
}
