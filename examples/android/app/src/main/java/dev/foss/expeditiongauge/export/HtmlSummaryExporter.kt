package dev.foss.expeditiongauge.export

import dev.foss.expeditiongauge.data.db.entities.SessionEventEntity
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import org.json.JSONObject

object HtmlSummaryExporter {
    fun export(
        summary: SessionStatsSummary,
        markedEvents: List<SessionEventEntity> = emptyList(),
    ): String {
        val sparkBars = summary.sparklineLatG.take(40).joinToString("") { value ->
            val height = (value.coerceIn(0f, 2f) / 2f * 100).toInt().coerceIn(4, 100)
            """<div class="bar" style="height:${height}%"></div>"""
        }
        val eventRows = markedEvents.joinToString("") { event ->
            val payload = runCatching { JSONObject(event.payloadJson.orEmpty()) }.getOrNull()
            val beta = payload?.optDouble("betaDeg")?.let { "%.1f°".format(it) } ?: "—"
            val latG = payload?.optDouble("latG")?.let { "%.2f".format(it) } ?: "—"
            val slip = payload?.optDouble("slipRatio")?.let { "%.2f".format(it) } ?: "—"
            """<tr><td>${event.timestampMs}</td><td>$latG</td><td>$beta</td><td>$slip</td></tr>"""
        }
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="utf-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1"/>
              <title>${summary.name} — ExpeditionGauge</title>
              <style>
                body { font-family: system-ui, sans-serif; background: #000; color: #fff; margin: 1rem; }
                .card { border: 1px solid #ffcc00; border-radius: 8px; padding: 1rem; margin-bottom: 1rem; }
                .spark { display: flex; align-items: flex-end; gap: 2px; height: 48px; }
                .bar { flex: 1; background: #33cc33; min-width: 4px; }
                h1 { color: #ffcc00; font-size: 1.25rem; }
                table { width: 100%; border-collapse: collapse; }
                td, th { border: 1px solid #666; padding: .35rem; font-size: .85rem; }
              </style>
            </head>
            <body>
              <h1>${summary.name}</h1>
              <div class="card">
                <p>Duration: ${summary.durationMs / 1000}s</p>
                <p>Peak latG: ${fmt(summary.peakLatG)}</p>
                <p>Max β: ${summary.maxBetaDeg?.let { "%.1f°".format(it) } ?: "—"}</p>
                <p>Best lap: ${summary.bestLapMs?.let { formatLap(it) } ?: "—"}</p>
                <p>Slip events: ${summary.slipEventCount}</p>
                <p>Marked events: ${summary.eventCount}</p>
              </div>
              <div class="card">
                <p>latG sparkline</p>
                <div class="spark">$sparkBars</div>
              </div>
              <div class="card">
                <p>Marked event snapshots</p>
                <table>
                  <tr><th>Time ms</th><th>latG</th><th>β</th><th>Slip</th></tr>
                  $eventRows
                </table>
              </div>
              <p>Generated locally by ExpeditionGauge — no cloud upload.</p>
            </body>
            </html>
        """.trimIndent()
    }

    fun exportComparison(left: SessionStatsSummary, right: SessionStatsSummary): String {
        return """
            <!DOCTYPE html>
            <html lang="en"><head><meta charset="utf-8"/><title>Compare — ExpeditionGauge</title>
            <style>body{background:#000;color:#fff;font-family:system-ui;padding:1rem}
            table{border-collapse:collapse;width:100%}td,th{border:1px solid #ffcc00;padding:.5rem}</style></head>
            <body><h1>Session comparison</h1>
            <table>
              <tr><th>Metric</th><th>${left.name}</th><th>${right.name}</th></tr>
              <tr><td>Peak latG</td><td>${fmt(left.peakLatG)}</td><td>${fmt(right.peakLatG)}</td></tr>
              <tr><td>Max β</td><td>${fmt(left.maxBetaDeg)}</td><td>${fmt(right.maxBetaDeg)}</td></tr>
              <tr><td>Best lap</td><td>${left.bestLapMs?.let { formatLap(it) } ?: "—"}</td><td>${right.bestLapMs?.let { formatLap(it) } ?: "—"}</td></tr>
              <tr><td>Slip events</td><td>${left.slipEventCount}</td><td>${right.slipEventCount}</td></tr>
              <tr><td>Marked events</td><td>${left.eventCount}</td><td>${right.eventCount}</td></tr>
            </table></body></html>
        """.trimIndent()
    }

    private fun fmt(value: Float?): String = value?.let { "%.2f".format(it) } ?: "—"

    private fun formatLap(ms: Long): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        val frac = (ms % 1000) / 10
        return if (min > 0) "%d:%02d.%02d".format(min, sec, frac) else "%d.%02d".format(sec, frac)
    }
}
