package dev.foss.expeditiongauge.gps

import android.location.Location
import android.location.LocationManager
import android.os.Handler

/** Resolves live altitude: GPS MSL when strong, USGS DEM when GNSS vertical quality is weak. */
object PhoneFixAltitude {
    fun gpsMslMeters(location: Location, lastGpsWithAlt: Location?): Double {
        if (location.hasAltitude()) return AltitudeNormalizer.fromLocation(location)
        return lastGpsWithAlt?.takeIf { it.hasAltitude() }?.let { AltitudeNormalizer.fromLocation(it) } ?: 0.0
    }

    fun preferDem(location: Location, sats: Int?, verticalAccuracyM: Float?): Boolean {
        val fromNetwork = location.provider == LocationManager.NETWORK_PROVIDER
        return fromNetwork || AltitudeSourcePolicy.preferDemElevation(sats, verticalAccuracyM)
    }

    fun resolve(
        location: Location,
        lastGpsWithAlt: Location?,
        sats: Int?,
        verticalAccuracyM: Float?,
        demElevation: DemElevationLookup,
    ): Pair<Double, Boolean> {
        val gpsAlt = gpsMslMeters(location, lastGpsWithAlt)
        val preferDem = preferDem(location, sats, verticalAccuracyM)
        val dem = demElevation.cachedNear(location.latitude, location.longitude)
        return AltitudeSourcePolicy.resolveMeters(gpsAlt, dem, preferDem) to preferDem
    }

    fun requestDemRefresh(
        demElevation: DemElevationLookup,
        location: Location,
        fallbackGpsAlt: Double,
        mainHandler: Handler,
        latestLocation: () -> Location?,
        onAltitude: (Double) -> Unit,
    ) {
        demElevation.requestAsync(location.latitude, location.longitude) { demMeters ->
            mainHandler.post {
                val latest = latestLocation() ?: return@post
                val gpsAlt = if (latest.hasAltitude()) {
                    AltitudeNormalizer.fromLocation(latest)
                } else {
                    fallbackGpsAlt
                }
                onAltitude(AltitudeSourcePolicy.resolveMeters(gpsAlt, demMeters, preferDem = true))
            }
        }
    }
}
