package dev.foss.expeditiongauge.share

import dev.foss.expeditiongauge.stats.SessionStatsSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ShareCardGeneratorTest {
    @Test
    fun generate_producesExpectedDimensions() {
        val summary = SessionStatsSummary(
            sessionId = 1L,
            name = "Test lap",
            durationMs = 90_000,
            maxBetaDeg = 12f,
            peakLatG = 1.1f,
            slipEventCount = 2,
            eventCount = 1,
            routeThumb = listOf(0f to 1f, 0.5f to 0.5f, 1f to 0f),
        )
        val bitmap = ShareCardGenerator.generate(summary)
        assertEquals(ShareCardGenerator.WIDTH, bitmap.width)
        assertEquals(ShareCardGenerator.HEIGHT, bitmap.height)
        assertTrue(bitmap.byteCount > 0)
    }
}
