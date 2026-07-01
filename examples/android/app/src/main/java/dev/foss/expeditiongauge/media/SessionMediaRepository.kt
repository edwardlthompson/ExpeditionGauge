package dev.foss.expeditiongauge.media

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dev.foss.expeditiongauge.data.db.dao.SessionMediaDao
import dev.foss.expeditiongauge.data.db.entities.SessionMediaEntity
import dev.foss.expeditiongauge.data.db.entities.SessionMediaKind
import dev.foss.expeditiongauge.settings.MediaCompressionQuality
import java.io.File

class SessionMediaRepository(
    private val context: Context,
    private val mediaDao: SessionMediaDao,
) {
    fun mediaDir(sessionId: Long): File {
        val dir = File(context.filesDir, "sessions/$sessionId/media")
        dir.mkdirs()
        return dir
    }

    fun createCaptureTarget(sessionId: Long): Pair<File, Uri> {
        val file = File(mediaDir(sessionId), "capture_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        return file to uri
    }

    suspend fun attachPhotoFromFile(
        sessionId: Long,
        timestampMs: Long,
        sourceFile: File,
        quality: MediaCompressionQuality,
    ): Long {
        val fileName = "photo_${timestampMs}.jpg"
        val dest = File(mediaDir(sessionId), fileName)
        val bytes = MediaCompressor.compressPhoto(sourceFile, dest, quality)
        return mediaDao.insert(
            SessionMediaEntity(
                sessionId = sessionId,
                timestampMs = timestampMs,
                fileName = fileName,
                mimeType = "image/jpeg",
                mediaKind = SessionMediaKind.PHOTO,
                bytesOnDisk = bytes,
            ),
        )
    }

    suspend fun attachPhotoFromUri(
        sessionId: Long,
        timestampMs: Long,
        uri: Uri,
        quality: MediaCompressionQuality,
    ): Long {
        val temp = File(context.cacheDir, "media_import_${System.currentTimeMillis()}.jpg")
        MediaCompressor.copyFromUri(context, uri, temp)
        return try {
            attachPhotoFromFile(sessionId, timestampMs, temp, quality)
        } finally {
            temp.delete()
        }
    }

    suspend fun attachStubPhoto(sessionId: Long, timestampMs: Long): Long {
        val fileName = "photo_${timestampMs}.jpg"
        val dest = File(mediaDir(sessionId), fileName)
        dest.writeBytes(STUB_JPEG)
        return mediaDao.insert(
            SessionMediaEntity(
                sessionId = sessionId,
                timestampMs = timestampMs,
                fileName = fileName,
                mimeType = "image/jpeg",
                mediaKind = SessionMediaKind.PHOTO,
                bytesOnDisk = dest.length(),
            ),
        )
    }

    suspend fun listForSession(sessionId: Long): List<SessionMediaEntity> =
        mediaDao.getBySession(sessionId)

    fun resolveFile(entity: SessionMediaEntity): File =
        File(mediaDir(entity.sessionId), entity.fileName)

    suspend fun deleteSessionFiles(sessionId: Long) {
        mediaDao.deleteForSession(sessionId)
        File(context.filesDir, "sessions/$sessionId/media").deleteRecursively()
    }

    suspend fun deleteSession(sessionId: Long) {
        deleteSessionFiles(sessionId)
    }

    suspend fun totalStorageBytes(): Long = mediaDao.totalBytes()

    companion object {
        /** Minimal valid JPEG for ADB stub attach. */
        private val STUB_JPEG = byteArrayOf(
            0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xDB.toByte(), 0x00.toByte(), 0x43.toByte(), 0x00.toByte(),
            0xFF.toByte(), 0xD9.toByte(),
        )
    }
}
