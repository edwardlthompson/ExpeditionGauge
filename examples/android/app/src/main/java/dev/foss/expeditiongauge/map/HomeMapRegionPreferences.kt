package dev.foss.expeditiongauge.map

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class HomeMapRegion(
    val centerLat: Double,
    val centerLon: Double,
    val radiusKm: Float,
) {
    fun bounds(): MapRegionBounds = MapRegionBounds.fromCenterRadiusKm(centerLat, centerLon, radiusKm)
}

private val Context.homeMapDataStore by preferencesDataStore("home_map_region")

class HomeMapRegionPreferences(private val context: Context) {
    private val latKey = doublePreferencesKey("home_center_lat")
    private val lonKey = doublePreferencesKey("home_center_lon")
    private val radiusKey = floatPreferencesKey("home_radius_km")

    val region: Flow<HomeMapRegion?> = context.homeMapDataStore.data.map { prefs ->
        val lat = prefs[latKey] ?: return@map null
        val lon = prefs[lonKey] ?: return@map null
        val radius = prefs[radiusKey] ?: DEFAULT_RADIUS_KM
        HomeMapRegion(lat, lon, radius)
    }

    suspend fun setRegion(centerLat: Double, centerLon: Double, radiusKm: Float = DEFAULT_RADIUS_KM) {
        context.homeMapDataStore.edit {
            it[latKey] = centerLat
            it[lonKey] = centerLon
            it[radiusKey] = radiusKm.coerceIn(MIN_RADIUS_KM, MAX_RADIUS_KM)
        }
    }

    suspend fun clear() {
        context.homeMapDataStore.edit { it.clear() }
    }

    companion object {
        const val DEFAULT_RADIUS_KM = 25f
        const val MIN_RADIUS_KM = 5f
        const val MAX_RADIUS_KM = 120f
    }
}
