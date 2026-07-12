package dev.foss.expeditiongauge.gps

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Cached USGS DEM elevation lookup for weak-GNSS altitude.
 * Network-only; fails open (returns null) when offline or outside coverage.
 */
class DemElevationLookup(
    private val scope: CoroutineScope,
    private val client: OkHttpClient = defaultClient(),
) {
    private val mutex = Mutex()
    private var cachedLat: Double? = null
    private var cachedLon: Double? = null
    private var cachedMeters: Double? = null
    private var lastRequestMs: Long = 0L
    private var inFlight: Job? = null

    fun cachedNear(latitude: Double, longitude: Double): Double? {
        val clat = cachedLat ?: return null
        val clon = cachedLon ?: return null
        val meters = cachedMeters ?: return null
        if (abs(clat - latitude) > CELL_DEG || abs(clon - longitude) > CELL_DEG) return null
        return meters
    }

    fun requestAsync(latitude: Double, longitude: Double, onResult: (Double) -> Unit) {
        val now = System.currentTimeMillis()
        cachedNear(latitude, longitude)?.let {
            onResult(it)
            return
        }
        if (now - lastRequestMs < MIN_REQUEST_INTERVAL_MS && inFlight?.isActive == true) return
        lastRequestMs = now
        inFlight?.cancel()
        inFlight = scope.launch {
            val meters = fetchMeters(latitude, longitude) ?: return@launch
            mutex.withLock {
                cachedLat = latitude
                cachedLon = longitude
                cachedMeters = meters
            }
            onResult(meters)
        }
    }

    private suspend fun fetchMeters(latitude: Double, longitude: Double): Double? =
        withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder()
                    .url(UsgsEpqsClient.buildUrlMeters(latitude, longitude))
                    .header("User-Agent", "ExpeditionGauge/FOSS")
                    .get()
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@runCatching null
                    val body = response.body.string()
                    UsgsEpqsClient.parseElevationMeters(body)
                }
            }.getOrNull()
        }

    companion object {
        /** ~110 m cell — reuse DEM while walking around the same spot. */
        const val CELL_DEG = 0.001
        const val MIN_REQUEST_INTERVAL_MS = 8_000L

        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .build()
    }
}
