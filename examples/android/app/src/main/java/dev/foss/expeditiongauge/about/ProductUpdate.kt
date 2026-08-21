package dev.foss.expeditiongauge.about

/** Product installer updates + one-time donate nudge. Prefs stay device-local. */
object ProductUpdate {
    const val MS_DAY = 86_400_000L
    const val RELEASES_API =
        "https://api.github.com/repos/edwardlthompson/ExpeditionGauge/releases/latest"
    const val RELEASES_PAGE =
        "https://github.com/edwardlthompson/ExpeditionGauge/releases/latest"

    data class NamedAsset(val name: String, val url: String)
    data class ProductAsset(val version: String, val url: String)

    fun shouldCheckDaily(lastCheckAt: Long?, now: Long): Boolean {
        if (lastCheckAt == null || lastCheckAt < 0L) return true
        return now - lastCheckAt >= MS_DAY
    }

    fun isNewerVersion(current: String, latest: String): Boolean {
        fun parts(v: String) = v.split('.').map { it.toIntOrNull() ?: 0 }
        val a = parts(current)
        val b = parts(latest)
        for (i in 0..2) {
            val diff = (a.getOrElse(i) { 0 }) - (b.getOrElse(i) { 0 })
            if (diff != 0) return diff < 0
        }
        return false
    }

    fun parseAssetVersion(name: String): String? {
        val src = name.trim()
        val patterns = listOf(
            Regex("expeditiongauge-(\\d+\\.\\d+\\.\\d+)(?:-foss)?\\.apk", RegexOption.IGNORE_CASE),
            Regex("expeditiongauge-(\\d+\\.\\d+\\.\\d+)-x64-setup\\.exe", RegexOption.IGNORE_CASE),
        )
        for (re in patterns) {
            re.find(src)?.let { return it.groupValues[1] }
        }
        return null
    }

    fun selectProductAsset(assets: List<NamedAsset>): ProductAsset? {
        for (asset in assets) {
            val version = parseAssetVersion(asset.name) ?: continue
            if (asset.url.isNotBlank()) return ProductAsset(version, asset.url)
        }
        return null
    }

    /** First launch records the version; later version changes get one donate note. */
    fun shouldNudgeDonate(lastSeenVersion: String?, currentVersion: String): Boolean {
        if (currentVersion.isBlank()) return false
        if (lastSeenVersion.isNullOrBlank()) return false
        return lastSeenVersion.trim() != currentVersion.trim()
    }

    fun shouldPromptUpdate(
        currentVersion: String,
        latestVersion: String?,
        dismissedVersion: String?,
    ): Boolean {
        if (latestVersion.isNullOrBlank()) return false
        if (!isNewerVersion(currentVersion, latestVersion)) return false
        if (dismissedVersion == latestVersion) return false
        return true
    }
}
