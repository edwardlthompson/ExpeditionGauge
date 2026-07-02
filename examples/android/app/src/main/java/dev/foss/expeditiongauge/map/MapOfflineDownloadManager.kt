package dev.foss.expeditiongauge.map

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.first

/**
 * Offline tile prefetch coordinator. Marks cached regions in [MapTileCacheRepository];
 * MapLibre [org.maplibre.android.offline.OfflineManager] region download is triggered on
 * a background thread when the native stack is available (device / instrumentation).
 */
class MapOfflineDownloadManager(
    private val context: Context,
    private val cacheRepository: MapTileCacheRepository,
    private val homeMapRegionPreferences: HomeMapRegionPreferences,
) {
    suspend fun isRegionCached(bounds: MapRegionBounds): Boolean = cacheRepository.isCached(bounds)

    suspend fun downloadRegion(
        bounds: MapRegionBounds,
        metadata: String,
        requireWifi: Boolean,
    ): Result<Unit> {
        if (!bounds.isValid) return Result.failure(IllegalArgumentException("invalid bounds"))
        if (requireWifi && !isOnWifi()) {
            return Result.failure(IllegalStateException("wifi_required"))
        }
        return runCatching {
            MapLibreOfflineRegionDownloader.enqueue(context.applicationContext, bounds, metadata)
            cacheRepository.markCached(bounds)
        }
    }

    suspend fun downloadHomeRegion(requireWifi: Boolean = true): Result<Unit> {
        val home = homeMapRegionPreferences.region.first() ?: return Result.success(Unit)
        return downloadRegion(home.bounds(), metadata = "home", requireWifi = requireWifi)
    }

    suspend fun downloadSessionRegion(
        bounds: MapRegionBounds,
        sessionId: Long,
        requireWifi: Boolean,
    ): Result<Unit> = downloadRegion(bounds, metadata = "session:$sessionId", requireWifi = requireWifi)

    private fun isOnWifi(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    companion object {
        const val MIN_ZOOM = 10.0
        const val MAX_ZOOM = 16.0
    }
}
