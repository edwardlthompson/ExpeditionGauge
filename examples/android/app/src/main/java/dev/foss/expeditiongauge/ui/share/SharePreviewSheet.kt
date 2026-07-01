package dev.foss.expeditiongauge.ui.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.share.ShareCardGenerator
import dev.foss.expeditiongauge.share.ShareExportLauncher
import dev.foss.expeditiongauge.stats.SessionStatsSummary
import dev.foss.expeditiongauge.ui.theme.GaugeScaleWhite
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import java.io.File

data class SharePreviewRequest(
    val videoPath: String,
    val summary: SessionStatsSummary,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharePreviewSheet(
    request: SharePreviewRequest,
    onDismiss: () -> Unit,
    onShared: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val cardBitmap = remember(request.summary) { ShareCardGenerator.generate(request.summary) }
    val videoFile = remember(request.videoPath) { File(request.videoPath) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("share_preview_sheet"),
    ) {
        Column(
            modifier = Modifier
                .padding(SpacingMd)
                .testTag("share_preview_content"),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Text(
                text = stringResource(R.string.share_preview_title),
                color = GaugeYellow,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.share_preview_subtitle),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(R.string.share_preview_card_label),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.labelMedium,
            )
            Image(
                bitmap = cardBitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.share_preview_card_label),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp)
                    .testTag("share_preview_card"),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = stringResource(R.string.share_preview_video_label, videoFile.name),
                color = GaugeScaleWhite,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("share_preview_video_label"),
            )
            Button(
                onClick = {
                    val cardFile = File(context.cacheDir, "exports/share_card_${request.summary.sessionId}.png")
                    ShareCardGenerator.writeToFile(request.summary, cardFile)
                    ShareExportLauncher.shareVideoAndCard(context, videoFile, cardFile)
                    onShared()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("share_preview_confirm")
                    .semantics { contentDescription = "Share video and card" },
            ) {
                Text(stringResource(R.string.share_preview_confirm))
            }
        }
    }
}
