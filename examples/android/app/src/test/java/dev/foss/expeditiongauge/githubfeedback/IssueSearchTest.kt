package dev.foss.expeditiongauge.githubfeedback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IssueSearchTest {
    @Test
    fun placeholderNeverFetches() {
        var called = false
        val out = IssueSearch.search("OWNER/REPO", "a1b2c3d4e5f6", 10_000L, null) {
            called = true
            "{}"
        }
        assertFalse(called)
        assertFalse(out.fetched)
        assertTrue(out.titles.isEmpty())
    }

    @Test
    fun cooldownSkipsSecondFetch() {
        var calls = 0
        val first = IssueSearch.search(
            "edwardlthompson/ExpeditionGauge",
            "a1b2c3d4e5f6",
            1_000L,
            null,
        ) {
            calls += 1
            """{"items":[{"title":"[crash] a1b2c3d4e5f6 Error"}]}"""
        }
        val second = IssueSearch.search(
            "edwardlthompson/ExpeditionGauge",
            "a1b2c3d4e5f6",
            30_000L,
            1_000L,
        ) {
            calls += 1
            "{}"
        }
        assertEquals(1, calls)
        assertTrue(first.fetched)
        assertEquals(listOf("[crash] a1b2c3d4e5f6 Error"), first.titles)
        assertFalse(second.fetched)
    }

    @Test
    fun timeoutOrForbiddenReturnsEmpty() {
        val out = IssueSearch.search(
            "edwardlthompson/ExpeditionGauge",
            "a1b2c3d4e5f6",
            5_000L,
            null,
        ) { throw RuntimeException("403") }
        assertTrue(out.fetched)
        assertTrue(out.titles.isEmpty())
    }
}
