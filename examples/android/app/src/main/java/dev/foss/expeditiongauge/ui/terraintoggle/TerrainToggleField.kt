package dev.foss.expeditiongauge.ui.terraintoggle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.R
import dev.foss.expeditiongauge.settings.TerrainToggleStore
import dev.foss.expeditiongauge.ui.settings.SettingsSwitchRow
import kotlinx.coroutines.launch

@Composable
fun TerrainToggleField(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { TerrainToggleStore(context) }
    val enabled by store.enabled.collectAsStateWithLifecycle(false)
    SettingsSwitchRow(
        label = stringResource(R.string.terrain_toggle),
        checked = enabled,
        onCheckedChange = { value -> scope.launch { store.setEnabled(value) } },
        modifier = modifier.testTag("terrain_toggle"),
    )
}
