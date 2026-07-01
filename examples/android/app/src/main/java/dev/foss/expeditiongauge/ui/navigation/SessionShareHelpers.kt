package dev.foss.expeditiongauge.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.ui.share.SharePreviewRequest
import java.io.File

internal fun shareHtmlSummary(context: Context, html: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/html"
        putExtra(Intent.EXTRA_TEXT, html)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.stats_export)))
}

internal fun shareExportFile(context: Context, file: File, mimeType: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.stats_export)))
}

internal fun resolveVideoShare(
    context: Context,
    path: String,
    sessionId: Long?,
    statsSummaries: List<SessionStatsSummary>,
    onShowPreview: (SharePreviewRequest) -> Unit,
) {
    if (FeatureFlags.sharingPolishEnabled) {
        val summary = statsSummaries.firstOrNull { it.sessionId == sessionId }
        if (summary != null) {
            onShowPreview(SharePreviewRequest(path, summary))
            return
        }
    }
    shareExportFile(context, File(path), "video/mp4")
}
