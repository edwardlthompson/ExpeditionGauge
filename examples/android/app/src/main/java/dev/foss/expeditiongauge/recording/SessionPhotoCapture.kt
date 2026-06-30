package dev.foss.expeditiongauge.recording

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object SessionPhotoCapture {
    fun createPhotoFile(context: Context, sessionId: Long): File {
        val dir = File(context.filesDir, "sessions/$sessionId")
        dir.mkdirs()
        return File(dir, "photo.jpg")
    }

    fun photoUri(context: Context, sessionId: Long): Uri {
        val file = createPhotoFile(context, sessionId)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
    }

    fun localUri(context: Context, sessionId: Long): String =
        createPhotoFile(context, sessionId).toURI().toString()

    fun attachStub(sessionId: Long): String = SessionPhotoStub.placeholderUri(sessionId)
}
