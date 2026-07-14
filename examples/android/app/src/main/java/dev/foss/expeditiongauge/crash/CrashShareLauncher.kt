package dev.foss.expeditiongauge.crash

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider

object CrashShareLauncher {
    fun shareLastCrash(context: Context): Boolean {
        val store = CrashLogStore.fromContext(context)
        val file = store.file()
        if (!file.isFile) return false
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, context.getString(dev.foss.expeditiongauge.R.string.settings_crash_share_subject))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(dev.foss.expeditiongauge.R.string.settings_crash_share),
            ),
        )
        return true
    }
}
