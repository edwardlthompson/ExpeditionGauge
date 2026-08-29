package dev.foss.expeditiongauge.ui.feedback

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.display.highRefreshScroll
import dev.foss.expeditiongauge.ui.navigation.GaugeBackHandler
import dev.foss.expeditiongauge.feedback.FeedbackPreview
import dev.foss.expeditiongauge.githubfeedback.IssueFormUrl
import dev.foss.expeditiongauge.privacyreport.FingerprintCrash

@Composable
fun FeedbackScreen(
    kind: String,
    initialDescription: String = "",
    stack: String? = null,
    online: Boolean,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf(initialDescription) }
    val preview = FeedbackPreview.text(kind, description, stack)
    val canSubmit = FeedbackPreview.canSubmit(description, stack)
    GaugeBackHandler(onBack = onDismiss)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(titleFor(kind))) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .highRefreshScroll(),
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.feedback_description)) },
                    modifier = Modifier.testTag("feedback_description"),
                )
                Text(text = preview, modifier = Modifier.testTag("feedback_preview"))
                if (!online) {
                    Text(stringResource(R.string.feedback_offline))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { copyText(context, preview) },
                modifier = Modifier.testTag("feedback_copy"),
            ) { Text(stringResource(R.string.feedback_copy)) }
            TextButton(
                onClick = {
                    openGithub(context, kind, description, stack)
                    onDismiss()
                },
                enabled = online && canSubmit,
                modifier = Modifier.testTag("feedback_github"),
            ) { Text(stringResource(R.string.feedback_open_github)) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("feedback_discard"),
            ) { Text(stringResource(R.string.feedback_discard)) }
        },
        modifier = Modifier.testTag("feedback_dialog"),
    )
}

private fun titleFor(kind: String): Int = when (kind) {
    "feature" -> R.string.feedback_request_feature
    "crash" -> R.string.feedback_review_crash
    else -> R.string.feedback_report_bug
}

private fun copyText(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("feedback", text))
}

private fun openGithub(context: Context, kind: String, description: String, stack: String?) {
    val template = if (kind == "feature") "feature_request.yml" else "bug_report.yml"
    val title = if (kind == "crash") {
        IssueFormUrl.crashTitle(FingerprintCrash.of(stack), stack?.lineSequence()?.firstOrNull() ?: "Error")
    } else {
        "[$kind]: "
    }
    val built = IssueFormUrl.build(
        IssueFormUrl.DEFAULT_REPO,
        template,
        mapOf(
            "title" to title,
            "description" to description,
            "stack" to (stack ?: ""),
        ),
    )
    built.clipboardMarkdown?.let { copyText(context, it) }
    if (built.url.startsWith("https://")) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(built.url)))
    }
}
