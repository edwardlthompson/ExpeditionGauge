package dev.foss.expeditiongauge.media

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import dev.foss.expeditiongauge.settings.MediaCompressionQuality
import java.io.File
import java.io.FileOutputStream

object MediaCompressor {
    fun compressPhoto(source: File, dest: File, quality: MediaCompressionQuality): Long {
        if (quality == MediaCompressionQuality.ORIGINAL) {
            source.copyTo(dest, overwrite = true)
            return dest.length()
        }
        val bitmap = BitmapFactory.decodeFile(source.absolutePath) ?: run {
            source.copyTo(dest, overwrite = true)
            return dest.length()
        }
        FileOutputStream(dest).use { out ->
            bitmap.compress(
                android.graphics.Bitmap.CompressFormat.JPEG,
                quality.jpegQuality,
                out,
            )
        }
        bitmap.recycle()
        return dest.length()
    }

    fun copyFromUri(context: Context, uri: Uri, dest: File): Long {
        context.contentResolver.openInputStream(uri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Unable to read $uri")
        return dest.length()
    }
}
