package dev.foss.expeditiongauge.about

import android.content.Context
import dev.foss.expeditiongauge.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class LatestRelease(
    val tag: String?,
    val assets: List<ReleaseAsset>,
    val htmlUrl: String? = null,
)

object ReleaseTagFetcher {
    fun loadReleaseRepo(context: Context): String? {
        return try {
            val json = context.assets.open("app-update.json").bufferedReader().use { it.readText() }
            val repo = JSONObject(json).optString("release_repo", "").trim()
            when {
                repo.isEmpty() -> KNOWN_REPO
                repo.equals("OWNER/REPO", ignoreCase = true) -> KNOWN_REPO
                repo.contains("OWNER/", ignoreCase = true) -> KNOWN_REPO
                else -> repo
            }
        } catch (_: Exception) {
            KNOWN_REPO
        }
    }

    suspend fun fetchLatestRelease(releaseRepo: String): LatestRelease? = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val url = URL("https://api.github.com/repos/$releaseRepo/releases/latest")
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/vnd.github+json")
            conn.setRequestProperty("User-Agent", "ExpeditionGauge/${BuildConfig.VERSION_NAME}")
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            if (conn.responseCode != HttpURLConnection.HTTP_OK) return@withContext null
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            parseLatestRelease(body)
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    fun parseLatestRelease(body: String): LatestRelease? {
        return try {
            val json = JSONObject(body)
            val tag = json.optString("tag_name", "").ifEmpty { null }
            val htmlUrl = json.optString("html_url", "").ifEmpty { ProductUpdate.RELEASES_PAGE }
            val assets = mutableListOf<ReleaseAsset>()
            val arr = json.optJSONArray("assets")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val item = arr.getJSONObject(i)
                    val name = item.optString("name", "")
                    val format = name.substringAfterLast('.', "bin")
                    assets.add(
                        ReleaseAsset(
                            format = format,
                            url = item.optString("browser_download_url", ""),
                            name = name,
                        ),
                    )
                }
            }
            LatestRelease(tag, assets, htmlUrl)
        } catch (_: Exception) {
            null
        }
    }

    private const val KNOWN_REPO = "edwardlthompson/ExpeditionGauge"
}
