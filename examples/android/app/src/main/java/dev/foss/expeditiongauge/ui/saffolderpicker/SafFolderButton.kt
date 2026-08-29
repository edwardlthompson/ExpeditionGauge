package dev.foss.expeditiongauge.ui.saffolderpicker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.saffolderpicker.SafFolderPicker

@Composable
fun SafFolderButton(modifier: Modifier = Modifier) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { /* persist handled by the tree intent flags */ }
    TextButton(
        onClick = { launcher.launch(null) },
        modifier = modifier.testTag("saf_folder_picker"),
    ) {
        Text(stringResource(R.string.saf_folder_picker))
    }
}
