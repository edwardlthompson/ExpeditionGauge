package dev.foss.expeditiongauge.map

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first

class MapTilePrefetchWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val kind = inputData.getString(KEY_KIND) ?: return Result.failure()
        val cache = MapTileCacheRepository(applicationContext)
        val homePrefs = HomeMapRegionPreferences(applicationContext)
        val manager = MapOfflineDownloadManager(applicationContext, cache, homePrefs)
        return when (kind) {
            KIND_HOME -> {
                val result = manager.downloadHomeRegion(requireWifi = true)
                if (result.isSuccess) Result.success() else Result.retry()
            }
            KIND_SESSION -> {
                val sessionId = inputData.getLong(KEY_SESSION_ID, -1L)
                if (sessionId < 0) return Result.failure()
                val minLat = inputData.getDouble(KEY_MIN_LAT, Double.NaN)
                val maxLat = inputData.getDouble(KEY_MAX_LAT, Double.NaN)
                val minLon = inputData.getDouble(KEY_MIN_LON, Double.NaN)
                val maxLon = inputData.getDouble(KEY_MAX_LON, Double.NaN)
                val bounds = MapRegionBounds(minLat, maxLat, minLon, maxLon)
                if (!bounds.isValid) return Result.failure()
                val allowCellular = cache.allowCellularDownloads.first()
                val requireWifi = !allowCellular
                val result = manager.downloadSessionRegion(bounds, sessionId, requireWifi = requireWifi)
                if (result.isSuccess) Result.success() else Result.retry()
            }
            else -> Result.failure()
        }
    }

    companion object {
        const val KEY_KIND = "kind"
        const val KIND_HOME = "home"
        const val KIND_SESSION = "session"
        const val KEY_SESSION_ID = "session_id"
        const val KEY_MIN_LAT = "min_lat"
        const val KEY_MAX_LAT = "max_lat"
        const val KEY_MIN_LON = "min_lon"
        const val KEY_MAX_LON = "max_lon"

        private val wifiConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        private val connectedConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun enqueueHomePrefetch(context: Context) {
            val request = OneTimeWorkRequestBuilder<MapTilePrefetchWorker>()
                .setInputData(Data.Builder().putString(KEY_KIND, KIND_HOME).build())
                .setConstraints(wifiConstraints)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "map_prefetch_home",
                ExistingWorkPolicy.KEEP,
                request,
            )
        }

        fun enqueueSessionPrefetch(context: Context, sessionId: Long, bounds: MapRegionBounds) {
            val request = OneTimeWorkRequestBuilder<MapTilePrefetchWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(KEY_KIND, KIND_SESSION)
                        .putLong(KEY_SESSION_ID, sessionId)
                        .putDouble(KEY_MIN_LAT, bounds.minLat)
                        .putDouble(KEY_MAX_LAT, bounds.maxLat)
                        .putDouble(KEY_MIN_LON, bounds.minLon)
                        .putDouble(KEY_MAX_LON, bounds.maxLon)
                        .build(),
                )
                .setConstraints(connectedConstraints)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "map_prefetch_session_$sessionId",
                ExistingWorkPolicy.KEEP,
                request,
            )
        }
    }
}
