package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settings.MediaCompressionQuality
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsMediaOptions(
    compressionQuality: MediaCompressionQuality,
    onCompressionSelect: (MediaCompressionQuality) -> Unit,
    storageBytes: Long,
    modifier: Modifier = Modifier,
) {
    if (!FeatureFlags.mediaAttachmentsEnabled) return
    Text(text = stringResource(R.string.settings_media_heading), modifier = modifier.fillMaxWidth())
    Text(
        text = stringResource(R.string.settings_media_storage, formatStorageMb(storageBytes)),
        modifier = Modifier.testTag("settings_media_storage"),
    )
    Text(text = stringResource(R.string.settings_media_compression))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
        MediaCompressionQuality.entries.forEach { quality ->
            FilterChip(
                selected = compressionQuality == quality,
                onClick = { onCompressionSelect(quality) },
                label = { Text(compressionLabel(quality)) },
                modifier = Modifier.testTag("settings_media_compression_${quality.name.lowercase()}"),
            )
        }
    }
}

private fun formatStorageMb(bytes: Long): String =
    "%.1f MB".format(bytes / (1024.0 * 1024.0))

@Composable
private fun compressionLabel(quality: MediaCompressionQuality): String = when (quality) {
    MediaCompressionQuality.ORIGINAL -> stringResource(R.string.settings_media_compression_original)
    MediaCompressionQuality.BALANCED -> stringResource(R.string.settings_media_compression_balanced)
    MediaCompressionQuality.COMPACT -> stringResource(R.string.settings_media_compression_compact)
}
