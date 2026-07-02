package dev.foss.expeditiongauge.map

import android.content.Context
import org.maplibre.android.MapLibre
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.offline.OfflineManager
import org.maplibre.android.offline.OfflineRegion
import org.maplibre.android.offline.OfflineTilePyramidRegionDefinition

/** Starts MapLibre offline region download (best-effort; cache index updated separately). */
internal object MapLibreOfflineRegionDownloader {
    fun enqueue(context: Context, bounds: MapRegionBounds, metadata: String) {
        runCatching {
            MapLibre.getInstance(context.applicationContext)
            val manager = OfflineManager.getInstance(context.applicationContext)
            val pixelRatio = context.resources.displayMetrics.density
            val definition = OfflineTilePyramidRegionDefinition(
                MapStyleUrls.DEMO_STYLE,
                LatLngBounds.from(bounds.minLat, bounds.minLon, bounds.maxLat, bounds.maxLon),
                MapOfflineDownloadManager.MIN_ZOOM,
                MapOfflineDownloadManager.MAX_ZOOM,
                pixelRatio,
            )
            manager.createOfflineRegion(
                definition,
                metadata.toByteArray(),
                object : OfflineManager.CreateOfflineRegionCallback {
                    override fun onCreate(offlineRegion: OfflineRegion) {
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE)
                    }

                    override fun onError(error: String) = Unit
                },
            )
        }
    }
}
