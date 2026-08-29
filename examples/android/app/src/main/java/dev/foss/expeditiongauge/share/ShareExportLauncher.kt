package dev.foss.expeditiongauge.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.encryptedsessionzip.EncryptedSessionZip
import java.io.File

object ShareExportLauncher {
    fun shareVideoAndCard(context: Context, videoFile: File, cardFile: File) {
        val authority = "${context.packageName}.fileprovider"
        val videoUri = FileProvider.getUriForFile(context, authority, videoFile)
        val cardUri = FileProvider.getUriForFile(context, authority, cardFile)
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(videoUri, cardUri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_card_caption))
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_preview_title)))
    }
}
