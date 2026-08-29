package dev.foss.expeditiongauge.ui.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.foss.expeditiongauge.talkbackfeedback.TalkBackFeedback
import dev.foss.expeditiongauge.aboutossnotices.OssNotices
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.about.DonateLinks
import dev.foss.expeditiongauge.about.DonationsConfig
import dev.foss.expeditiongauge.display.highRefreshScroll
import dev.foss.expeditiongauge.ui.feedback.FeedbackScreen
import dev.foss.expeditiongauge.ui.navigation.GaugeBackHandler
import dev.foss.expeditiongauge.ui.theme.GaugeMenuSurface
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun AboutScreen(
    version: String,
    installedFormat: String,
    updateStatus: String,
    donations: DonationsConfig,
    canApplyUpdate: Boolean,
    onApplyUpdate: () -> Unit,
    onBack: () -> Unit,
    online: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    var feedbackKind by remember { mutableStateOf<String?>(null) }
    GaugeMenuSurface(modifier = modifier) {
        GaugeBackHandler(onBack = onBack)
        Column(
            modifier = Modifier
                .padding(SpacingMd)
                .verticalScroll(rememberScrollState())
                .highRefreshScroll(),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
        Text(
            text = stringResource(R.string.about_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        Text(text = stringResource(R.string.about_version, version), color = GaugeScaleWhite)
        Text(
            text = stringResource(R.string.about_oss_notices, OssNotices.summary()),
            color = GaugeScaleWhite,
        )
        Text(text = stringResource(R.string.about_format, installedFormat), color = GaugeScaleWhite)
        Text(text = updateStatus, color = GaugeScaleWhite)
        if (canApplyUpdate) {
            Button(onClick = onApplyUpdate) {
                Text(stringResource(R.string.about_update_apply))
            }
        }
        TextButton(
            onClick = { uriHandler.openUri(DonateLinks.VENMO_URL) },
            modifier = Modifier.testTag("about_donate"),
        ) {
            Text(stringResource(R.string.about_donate))
        }
        val extra = donations.links.filter {
            it.url != DonateLinks.VENMO_URL && !DonateLinks.isPlaceholderLink(it)
        }
        if (donations.enabled && extra.isNotEmpty()) {
            Text(text = donations.message, color = GaugeScaleWhite)
            extra.forEach { link ->
                Text(
                    text = link.label,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { uriHandler.openUri(link.url) },
                )
            }
        }
        TextButton(
            onClick = { feedbackKind = "bug" },
            modifier = Modifier
                .testTag("about_report_bug")
                .semantics { contentDescription = TalkBackFeedback.description("bug") },
        ) { Text(stringResource(R.string.feedback_report_bug)) }
        TextButton(
            onClick = { feedbackKind = "feature" },
            modifier = Modifier
                .testTag("about_request_feature")
                .semantics { contentDescription = TalkBackFeedback.description("feature") },
        ) { Text(stringResource(R.string.feedback_request_feature)) }
        Button(onClick = onBack) {
            Text(stringResource(R.string.about_close))
        }
        }
    }
    feedbackKind?.let { kind ->
        FeedbackScreen(kind = kind, online = online, onDismiss = { feedbackKind = null })
    }
}
