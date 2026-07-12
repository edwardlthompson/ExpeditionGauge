package dev.foss.expeditiongauge.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import androidx.core.content.ContextCompat

/** A-GPS inject + GNSS used-in-fix satellite count (A-GPS does not provide elevation). */
class PhoneGnssAssist(
    private val context: Context,
    private val locationManager: LocationManager,
    private val handler: Handler,
) {
    var usedSatellites: Int? = null
        private set

    private val gnssCallback = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            var used = 0
            for (i in 0 until status.satelliteCount) {
                if (status.usedInFix(i)) used++
            }
            usedSatellites = used
        }
    }

    fun injectAssistedGps() {
        runCatching {
            locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "force_xtra_injection", null)
            locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "force_time_injection", null)
        }
    }

    fun register() {
        if (!hasLocationPermission()) return
        runCatching { locationManager.registerGnssStatusCallback(gnssCallback, handler) }
    }

    fun unregister() {
        runCatching { locationManager.unregisterGnssStatusCallback(gnssCallback) }
    }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    fun verticalAccuracyOrNull(location: Location): Float? {
        if (Build.VERSION.SDK_INT < 26) return null
        return if (location.hasVerticalAccuracy()) location.verticalAccuracyMeters else null
    }

    fun satelliteCount(location: Location): Int? =
        usedSatellites ?: location.extras?.getInt("satellites", -1)?.takeIf { it >= 0 }
}
