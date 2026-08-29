package dev.foss.expeditiongauge.driftrunranking

import org.junit.Assert.assertEquals
import org.junit.Test

class DriftRunRankingTest {
    @Test
    fun favoritesBeatHighScoreThenScoreSorts() {
        assertEquals(24f, DriftRunRanking.score(20f, 2))
        val ranked = DriftRunRanking.rank(
            items = listOf(1L to 5f, 2L to 30f, 3L to 10f),
            favoriteIds = setOf(1L),
            idOf = { it.first },
            scoreOf = { it.second },
        )
        assertEquals(listOf(1L, 2L, 3L), ranked.map { it.first })
    }
}
