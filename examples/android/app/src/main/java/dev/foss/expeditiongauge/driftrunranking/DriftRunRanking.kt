package dev.foss.expeditiongauge.driftrunranking

/** Library drift score from max β plus slip events. */
object DriftRunRanking {
    fun score(maxBetaDeg: Float?, slipEventCount: Int): Float =
        (maxBetaDeg ?: 0f) + slipEventCount * 2f

    fun <T> rank(
        items: List<T>,
        favoriteIds: Set<Long> = emptySet(),
        idOf: (T) -> Long,
        scoreOf: (T) -> Float,
    ): List<T> = items.sortedWith(
        compareByDescending<T> { idOf(it) in favoriteIds }
            .thenByDescending { scoreOf(it) },
    )
}
