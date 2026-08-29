package dev.foss.expeditiongauge.markeventchapters

import dev.foss.expeditiongauge.data.db.entities.SessionEventEntity
import dev.foss.expeditiongauge.relivechapters.ReliveChapter
import dev.foss.expeditiongauge.relivechapters.ReliveChapters

/** Chapter list block for HTML session share. */
object MarkEventChaptersExport {
    fun htmlSection(events: List<SessionEventEntity>): String =
        htmlSection(ReliveChapters.fromEvents(events))

    fun htmlSection(chapters: List<ReliveChapter>): String {
        if (chapters.isEmpty()) return ""
        val rows = chapters.joinToString("") { chapter ->
            "<tr><td>${chapter.title}</td><td>${chapter.timestampMs}</td></tr>"
        }
        return """<div class="card"><p>Chapters</p><table><tr><th>Chapter</th><th>Time ms</th></tr>$rows</table></div>"""
    }
}
