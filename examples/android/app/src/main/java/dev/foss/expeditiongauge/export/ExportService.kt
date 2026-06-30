package dev.foss.expeditiongauge.export

import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ExportFormat { CSV, JSON, GPX }

class ExportService(private val database: ExpeditionGaugeDatabase) {
    private val sessionDao = database.recordingSessionDao()
    private val sampleDao = database.sampleDao()

    suspend fun exportSession(sessionId: Long, format: ExportFormat, outputDir: File): File {
        val session = sessionDao.getById(sessionId)
            ?: throw IllegalArgumentException("Session $sessionId not found")
        val samples = sampleDao.getBySession(sessionId)
        outputDir.mkdirs()
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date(session.startTimeMs))
        val file = File(outputDir, "session_${sessionId}_$stamp.${format.name.lowercase()}")
        file.writeText(
            when (format) {
                ExportFormat.CSV -> ExportFormatters.toCsv(session, samples)
                ExportFormat.JSON -> ExportFormatters.toJson(session, samples)
                ExportFormat.GPX -> ExportFormatters.toGpx(session, samples)
            },
        )
        return file
    }
}
