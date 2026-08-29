package dev.foss.expeditiongauge.ui.colorblind

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import dev.foss.expeditiongauge.colorblind.ColorblindHud
import dev.foss.expeditiongauge.colorblind.ColorblindHudMode
import dev.foss.expeditiongauge.settings.ColorblindHudStore
import kotlinx.coroutines.launch

@Composable
fun ColorblindHudField(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { ColorblindHudStore(context) }
    val mode by store.mode.collectAsStateWithLifecycle(ColorblindHudMode.NONE)
    Button(
        onClick = { scope.launch { store.cycle() } },
        modifier = modifier.fillMaxWidth().testTag("colorblind_hud_cycle"),
    ) {
        Text(stringResource(R.string.colorblind_hud_cycle, ColorblindHud.label(mode)))
    }
}
