package dev.foss.expeditiongauge.sessionfavorites

/** Sort and match starred library sessions. */
object SessionFavorites {
    fun parseIds(raw: String?): Set<Long> =
        raw?.split(',')?.mapNotNull { it.trim().toLongOrNull() }?.toSet().orEmpty()

    fun encodeIds(ids: Set<Long>): String = ids.sorted().joinToString(",")

    fun toggle(ids: Set<Long>, sessionId: Long): Set<Long> =
        if (sessionId in ids) ids - sessionId else ids + sessionId

    fun <T> favoritesFirst(
        items: List<T>,
        favoriteIds: Set<Long>,
        idOf: (T) -> Long,
    ): List<T> = items.sortedWith(compareByDescending { idOf(it) in favoriteIds })
}
