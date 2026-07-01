package dev.foss.expeditiongauge.ui.playback

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.recording.ActivityType
import dev.foss.expeditiongauge.recording.SessionMetadata
import dev.foss.expeditiongauge.recording.SessionMetadataRepository
import dev.foss.expeditiongauge.recording.SessionPhotoCapture
import dev.foss.expeditiongauge.ui.theme.GaugeYellow
import dev.foss.expeditiongauge.ui.theme.SpacingMd
import kotlinx.coroutines.launch

@Composable
fun SessionMetadataEditScreen(
    sessionId: Long,
    repository: SessionMetadataRepository,
    context: Context,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    onDeleteSession: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var notes by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var activityType by remember { mutableStateOf(ActivityType.DRIVE) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(sessionId) {
        repository.get(sessionId)?.let { meta ->
            notes = meta.notes.orEmpty()
            driverName = meta.driverName.orEmpty()
            conditions = meta.conditions.orEmpty()
            tagsText = meta.tags.joinToString(", ")
            photoUri = meta.photoUri
            activityType = meta.activityType
        }
    }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri = SessionPhotoCapture.localUri(context, sessionId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SpacingMd)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.session_metadata_edit_title),
            style = MaterialTheme.typography.headlineSmall,
            color = GaugeYellow,
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(stringResource(R.string.session_metadata_notes)) },
            modifier = Modifier.fillMaxWidth().testTag("session_metadata_notes"),
        )
        OutlinedTextField(
            value = driverName,
            onValueChange = { driverName = it },
            label = { Text(stringResource(R.string.session_metadata_driver)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = conditions,
            onValueChange = { conditions = it },
            label = { Text(stringResource(R.string.session_metadata_conditions)) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = tagsText,
            onValueChange = { tagsText = it },
            label = { Text(stringResource(R.string.session_metadata_tags)) },
            modifier = Modifier.fillMaxWidth().testTag("session_metadata_tags"),
        )
        if (FeatureFlags.activityLibraryEnabled) {
            Text(
                text = stringResource(R.string.session_metadata_activity),
                style = MaterialTheme.typography.titleSmall,
                color = GaugeYellow,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(SpacingMd),
            ) {
                ActivityType.entries.forEach { type ->
                    FilterChip(
                        selected = activityType == type,
                        onClick = { activityType = type },
                        label = { Text(activityTypeLabel(type)) },
                        modifier = Modifier.testTag("activity_type_${type.name}"),
                    )
                }
            }
        }
        Button(
            onClick = {
                scope.launch {
                    val tags = tagsText.split(',').map { it.trim() }.filter { it.isNotEmpty() }
                    repository.save(
                        sessionId,
                        SessionMetadata(
                            notes = notes.ifBlank { null },
                            driverName = driverName.ifBlank { null },
                            conditions = conditions.ifBlank { null },
                            tags = tags,
                            photoUri = photoUri,
                            activityType = activityType,
                        ),
                    )
                    onSaved()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("session_metadata_save")
                .semantics { contentDescription = "Save metadata" },
        ) {
            Text(stringResource(R.string.session_metadata_save))
        }
        Button(
            onClick = {
                val uri = SessionPhotoCapture.photoUri(context, sessionId)
                takePicture.launch(uri)
            },
            modifier = Modifier.fillMaxWidth().testTag("session_photo_capture"),
        ) {
            Text(stringResource(R.string.session_metadata_photo))
        }
        Button(
            onClick = { photoUri = SessionPhotoCapture.attachStub(sessionId) },
            modifier = Modifier.fillMaxWidth().testTag("session_photo_stub"),
        ) {
            Text(stringResource(R.string.session_metadata_photo_stub))
        }
        photoUri?.let {
            Text(text = stringResource(R.string.session_metadata_photo_attached), style = MaterialTheme.typography.bodySmall)
        }
        onDeleteSession?.let { delete ->
            Button(
                onClick = delete,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("session_delete")
                    .semantics { contentDescription = "Delete session" },
            ) {
                Text(stringResource(R.string.session_delete))
            }
        }
        Button(onClick = onBack) {
            Text(stringResource(R.string.settings_close))
        }
    }
}
