package dev.foss.expeditiongauge.export

import dev.foss.expeditiongauge.data.db.entities.SessionEventEntity
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class HtmlSummaryExporterTest {
    @Test
    fun exportIncludesMarkedEventSnapshot() {
        val summary = SessionStatsSummary(
            sessionId = 1,
            name = "Drift run",
            durationMs = 60_000,
            maxBetaDeg = 22f,
            peakLatG = 1.1f,
            slipEventCount = 2,
            eventCount = 1,
            sparklineLatG = listOf(0.2f, 0.8f, 1.1f),
        )
        val events = listOf(
            SessionEventEntity(
                sessionId = 1,
                timestampMs = 5000,
                eventType = "mark",
                payloadJson = """{"latG":0.9,"betaDeg":18.0,"slipRatio":0.12}""",
            ),
        )
        val html = HtmlSummaryExporter.export(summary, events)
        assertTrue(html.contains("Marked events: 1"))
        assertTrue(html.contains("latG sparkline"))
        assertTrue(html.contains("0.90"))
    }
}
