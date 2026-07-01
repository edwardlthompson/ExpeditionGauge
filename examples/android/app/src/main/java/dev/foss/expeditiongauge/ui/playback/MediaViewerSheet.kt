package dev.foss.expeditiongauge.ui.playback

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.data.db.entities.SessionMediaEntity
import dev.foss.expeditiongauge.media.SessionMediaRepository
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaViewerSheet(
    media: SessionMediaEntity?,
    repository: SessionMediaRepository,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (media == null) return
    val file: File = repository.resolveFile(media)
    val bitmap = remember(media.id, file.lastModified()) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    }
    ModalBottomSheet(onDismissRequest = onDismiss, modifier = modifier.testTag("media_viewer_sheet")) {
        Column(modifier = Modifier.padding(SpacingMd)) {
            Text(text = stringResource(R.string.media_viewer_title, media.timestampMs))
            bitmap?.let { image ->
                Image(
                    bitmap = image,
                    contentDescription = stringResource(R.string.media_viewer_photo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .testTag("media_viewer_image"),
                    contentScale = ContentScale.Fit,
                )
            }
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_close))
            }
        }
    }
}
