package dev.foss.expeditiongauge.gps

/** DEM altitude selection for external NMEA when satellite count is low. */
object ExternalFixAltitude {
    fun toState(
        fix: NmeaFix,
        heading: Float,
        speed: Float,
        demElevation: DemElevationLookup,
    ): FusedGpsState {
        val preferDem = AltitudeSourcePolicy.preferDemElevation(fix.numSatellites, verticalAccuracyM = null)
        val lat = fix.latitude
        val lon = fix.longitude
        val dem = if (lat != null && lon != null) demElevation.cachedNear(lat, lon) else null
        val altitude = if (preferDem && dem != null) dem else fix.altitudeM
        return FusedGpsState(
            source = GpsSource.EXTERNAL,
            fix = true,
            latitude = lat,
            longitude = lon,
            altitudeM = altitude,
            speedMps = speed,
            headingDeg = heading,
            hdop = fix.hdop,
            numSatellites = fix.numSatellites,
            fixQuality = fix.fixQuality,
        )
    }

    fun refreshDemIfNeeded(
        fix: NmeaFix,
        demElevation: DemElevationLookup,
        onDem: (Double) -> Unit,
    ) {
        val preferDem = AltitudeSourcePolicy.preferDemElevation(fix.numSatellites, verticalAccuracyM = null)
        val lat = fix.latitude
        val lon = fix.longitude
        if (!preferDem || lat == null || lon == null) return
        demElevation.requestAsync(lat, lon, onDem)
    }
}
