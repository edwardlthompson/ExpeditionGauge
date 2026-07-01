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
import kotlin.math.atan2
import kotlin.math.sqrt

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
        lastLocation = location
        val heading = if (location.hasBearing()) location.bearing else computeHeading(location)
        val speedMps = if (location.hasSpeed()) location.speed else 0f
        driftEstimator.onGpsSample(heading, speedMps)
        val snapshot = TelemetrySnapshot(
            timestampMs = System.currentTimeMillis(),
            speedMps = speedMps,
            headingDeg = heading,
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

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    private fun computeHeading(location: Location): Float {
        val prev = lastLocation ?: return 0f
        val dLon = Math.toRadians(location.longitude - prev.longitude)
        val lat1 = Math.toRadians(prev.latitude)
        val lat2 = Math.toRadians(location.latitude)
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        return Math.toDegrees(atan2(y, x)).toFloat().let { if (it < 0) it + 360f else it }
    }

    private fun sin(value: Double) = kotlin.math.sin(value)
    private fun cos(value: Double) = kotlin.math.cos(value)
}
