package dev.foss.expeditiongauge.map

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.foss.expeditiongauge.offlinetilecache.OfflineTileCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.mapCacheDataStore by preferencesDataStore("map_tile_cache")

/** Tracks downloaded region keys and user consent for cellular map downloads. */
class MapTileCacheRepository(private val context: Context) {
    private val cachedKeys = stringSetPreferencesKey("cached_region_keys")
    private val cachedBounds = stringSetPreferencesKey("cached_bounds")
    private val cellularKey = booleanPreferencesKey("allow_cellular_map_download")

    val allowCellularDownloads: Flow<Boolean> = context.mapCacheDataStore.data.map {
        it[cellularKey] ?: false
    }

    suspend fun setAllowCellularDownloads(allowed: Boolean) {
        context.mapCacheDataStore.edit { it[cellularKey] = allowed }
    }

    suspend fun markCached(bounds: MapRegionBounds) {
        context.mapCacheDataStore.edit { prefs ->
            prefs[cachedKeys] = OfflineTileCache.evictOldest(
                prefs[cachedKeys].orEmpty() + bounds.cacheKey(),
            ).toSet()
            val serialized = OfflineTileCache.evictOldest(prefs[cachedBounds].orEmpty() + serialize(bounds))
            prefs[cachedBounds] = serialized.toSet()
        }
    }

    suspend fun isCached(bounds: MapRegionBounds): Boolean {
        val entries = context.mapCacheDataStore.data.first()[cachedBounds].orEmpty()
        return entries.any { deserialize(it)?.contains(bounds) == true }
    }

    private fun serialize(bounds: MapRegionBounds): String =
        "${bounds.minLat},${bounds.minLon},${bounds.maxLat},${bounds.maxLon}"

    private fun deserialize(raw: String): MapRegionBounds? {
        val parts = raw.split(',')
        if (parts.size != 4) return null
        return runCatching {
            MapRegionBounds(
                minLat = parts[0].toDouble(),
                maxLat = parts[2].toDouble(),
                minLon = parts[1].toDouble(),
                maxLon = parts[3].toDouble(),
            )
        }.getOrNull()
    }
}
