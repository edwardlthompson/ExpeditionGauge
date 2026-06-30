package dev.foss.expeditiongauge.export

import android.content.Context
import dev.foss.expeditiongauge.data.db.ExpeditionGaugeDatabase
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

enum class EnhancedExportFormat { GPX, CSV, JSON, ZIP }

class EnhancedExportService(
    private val database: ExpeditionGaugeDatabase,
    private val exportService: ExportService,
    private val context: Context,
) {
    suspend fun exportSession(
        sessionId: Long,
        format: EnhancedExportFormat,
        outputDir: File,
    ): File = when (format) {
        EnhancedExportFormat.GPX -> exportService.exportSession(sessionId, ExportFormat.GPX, outputDir)
        EnhancedExportFormat.CSV -> exportService.exportSession(sessionId, ExportFormat.CSV, outputDir)
        EnhancedExportFormat.JSON -> exportService.exportSession(sessionId, ExportFormat.JSON, outputDir)
        EnhancedExportFormat.ZIP -> exportZipBundle(sessionId, outputDir)
    }

    private suspend fun exportZipBundle(sessionId: Long, outputDir: File): File {
        outputDir.mkdirs()
        val csv = exportService.exportSession(sessionId, ExportFormat.CSV, outputDir)
        val json = exportService.exportSession(sessionId, ExportFormat.JSON, outputDir)
        val gpx = exportService.exportSession(sessionId, ExportFormat.GPX, outputDir)
        val session = database.recordingSessionDao().getById(sessionId)
            ?: throw IllegalArgumentException("Session $sessionId not found")
        val manifest = JSONObject().apply {
            put("sessionId", sessionId)
            put("name", session.name)
            put("videoUri", session.videoUri)
            put("videoOffsetMs", session.videoOffsetMs)
            put("files", listOf(csv.name, json.name, gpx.name))
        }
        val manifestFile = File(outputDir, "session_${sessionId}_manifest.json")
        manifestFile.writeText(manifest.toString(2))
        val zipFile = File(outputDir, "session_${sessionId}_bundle.zip")
        ZipOutputStream(FileOutputStream(zipFile)).use { zip ->
            listOf(csv, json, gpx, manifestFile).forEach { file ->
                zip.putNextEntry(ZipEntry(file.name))
                FileInputStream(file).use { it.copyTo(zip) }
                zip.closeEntry()
            }
            session.videoUri?.let { uri ->
                runCatching {
                    context.contentResolver.openInputStream(android.net.Uri.parse(uri))?.use { input ->
                        zip.putNextEntry(ZipEntry("session_video.mp4"))
                        input.copyTo(zip)
                        zip.closeEntry()
                    }
                }
            }
        }
        return zipFile
    }
}
