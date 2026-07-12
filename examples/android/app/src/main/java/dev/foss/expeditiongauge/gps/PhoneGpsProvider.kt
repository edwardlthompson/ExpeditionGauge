package dev.foss.expeditiongauge.gps

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import dev.foss.expeditiongauge.drift.DriftAngleEstimator
import dev.foss.expeditiongauge.sensors.SensorPollScheduler
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot

class PhoneGpsProvider(
    context: Context,
    private val driftEstimator: DriftAngleEstimator,
    private val demElevation: DemElevationLookup,
    private val onPhoneFix: (TelemetrySnapshot) -> Unit,
) : LocationListener {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private val gnss = PhoneGnssAssist(context, locationManager, mainHandler)
    private var lastLocation: Location? = null
    private var lastGpsLocation: Location? = null

    fun start() {
        if (!gnss.hasLocationPermission()) return
        gnss.injectAssistedGps()
        gnss.register()
        try {
            requestProvider(LocationManager.GPS_PROVIDER)
            // Network: faster coarse fix + A-GPS assist; elevation still GPS/DEM.
            requestProvider(LocationManager.NETWORK_PROVIDER)
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { onLocationChanged(it) }
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let { onLocationChanged(it) }
        } catch (_: SecurityException) {
            // Permission not granted at runtime
        }
    }

    fun stop() {
        locationManager.removeUpdates(this)
        gnss.unregister()
    }

    override fun onLocationChanged(location: Location) {
        if (location.provider == LocationManager.NETWORK_PROVIDER && hasFreshGpsFix()) return
        val prev = lastLocation
        lastLocation = location
        if (location.provider == LocationManager.GPS_PROVIDER) lastGpsLocation = location
        val speedMps = if (location.hasSpeed()) location.speed else 0f
        val course = resolveCourse(prev, location, speedMps)
        driftEstimator.onGpsSample(course, speedMps)
        val sats = gnss.satelliteCount(location)
        val (altitude, preferDem) = PhoneFixAltitude.resolve(
            location, lastGpsLocation, sats, gnss.verticalAccuracyOrNull(location), demElevation,
        )
        publish(location, speedMps, course, altitude, sats)
        if (preferDem) {
            val gpsAlt = PhoneFixAltitude.gpsMslMeters(location, lastGpsLocation)
            PhoneFixAltitude.requestDemRefresh(
                demElevation, location, gpsAlt, mainHandler, { lastLocation },
            ) { demAlt ->
                val latest = lastLocation ?: return@requestDemRefresh
                val latestSpeed = if (latest.hasSpeed()) latest.speed else speedMps
                publish(latest, latestSpeed, course, demAlt, gnss.usedSatellites ?: sats)
            }
        }
    }

    override fun onProviderDisabled(provider: String) = Unit
    override fun onProviderEnabled(provider: String) = Unit
    @Deprecated("Deprecated in LocationListener")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit

    private fun requestProvider(provider: String) {
        if (!locationManager.isProviderEnabled(provider)) return
        locationManager.requestLocationUpdates(
            provider, SensorPollScheduler.PHONE_GPS_INTERVAL_MS, 0f, this,
        )
    }

    private fun hasFreshGpsFix(): Boolean {
        val gps = lastGpsLocation ?: return false
        val ageMs = (SystemClock.elapsedRealtimeNanos() - gps.elapsedRealtimeNanos) / 1_000_000L
        return ageMs in 0..15_000L
    }

    private fun publish(
        location: Location,
        speedMps: Float,
        course: Float,
        altitudeM: Double,
        sats: Int?,
    ) {
        onPhoneFix(
            TelemetrySnapshot(
                timestampMs = System.currentTimeMillis(),
                speedMps = speedMps,
                headingDeg = course,
                velocityHeadingDeg = course,
                latitude = location.latitude,
                longitude = location.longitude,
                altitudeM = altitudeM,
                gpsFix = true,
                gpsSource = "phone",
                numSatellites = sats,
                driftAngleDeg = driftEstimator.currentSample().driftAngleDeg,
            ),
        )
    }

    private fun resolveCourse(prev: Location?, location: Location, speedMps: Float): Float {
        if (prev != null) {
            val segmentM = GpsCourseLogic.distanceM(
                prev.latitude, prev.longitude, location.latitude, location.longitude,
            )
            val fromMove = GpsCourseLogic.bearingDeg(
                prev.latitude, prev.longitude, location.latitude, location.longitude,
            )
            if (GpsCourseLogic.isReliableCourse(speedMps, segmentM)) return fromMove
        }
        return if (location.hasBearing()) location.bearing else 0f
    }
}
