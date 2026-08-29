package dev.foss.expeditiongauge.offlinetilecache

/** Cap and FIFO-evict offline map region keys. */
object OfflineTileCache {
    const val MAX_REGIONS = 8

    fun evictOldest(entries: Collection<String>, max: Int = MAX_REGIONS): List<String> {
        val unique = entries.filter { it.isNotBlank() }.distinct()
        if (unique.size <= max) return unique
        return unique.takeLast(max)
    }

    fun isOverCap(count: Int, max: Int = MAX_REGIONS): Boolean = count > max

    fun usageLabel(count: Int, max: Int = MAX_REGIONS): String = "$count / $max"
}
