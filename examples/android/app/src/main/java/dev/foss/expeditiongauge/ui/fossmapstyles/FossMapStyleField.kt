package dev.foss.expeditiongauge.ui.fossmapstyles

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
import dev.foss.expeditiongauge.fossmapstyles.FossMapStyles
import dev.foss.expeditiongauge.settings.FossMapStyleStore
import kotlinx.coroutines.launch

@Composable
fun FossMapStyleField(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { FossMapStyleStore(context) }
    val id by store.styleId.collectAsStateWithLifecycle("demo")
    val style = FossMapStyles.byId(id)
    FossMapStyles.selectedId = style.id
    Button(
        onClick = {
            scope.launch { store.setStyleId(FossMapStyles.cycle(style.id).id) }
        },
        modifier = modifier.testTag("foss_map_style"),
    ) {
        Text(stringResource(R.string.foss_map_style_current, style.label))
    }
}
