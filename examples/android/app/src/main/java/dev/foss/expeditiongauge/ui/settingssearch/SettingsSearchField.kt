package dev.foss.expeditiongauge.ui.settingssearch

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settingssearch.SettingsSearch

@Composable
fun SettingsSearchField(
    query: String,
    onQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQuery,
        label = { Text(stringResource(R.string.settings_search)) },
        supportingText = { Text(SettingsSearch.match(query).joinToString()) },
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_search"),
        singleLine = true,
    )
}
