package dev.foss.expeditiongauge.ui.recording

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R

@Composable
fun AttachMediaFab(
    visible: Boolean,
    onAttachCamera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible || !FeatureFlags.mediaAttachmentsEnabled) return
    FloatingActionButton(
        onClick = onAttachCamera,
        modifier = modifier.testTag("attach_media_camera"),
        containerColor = MaterialTheme.colorScheme.secondary,
    ) {
        Icon(
            imageVector = Icons.Filled.PhotoCamera,
            contentDescription = stringResource(R.string.attach_media_camera),
        )
    }
}
