package dev.foss.expeditiongauge.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.foss.expeditiongauge.ui.navigation.GaugeBackHandler
import dev.foss.expeditiongauge.ui.theme.GaugeMenuSurface
import dev.foss.expeditiongauge.ui.theme.SpacingMd

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    actions: SettingsUiActions,
    modifier: Modifier = Modifier,
) {
    var category by remember { mutableStateOf<SettingsCategory?>(null) }
    GaugeMenuSurface(modifier = modifier) {
        GaugeBackHandler(
            onBack = {
                when (settingsBackTarget(category)) {
                    SettingsBackTarget.Exit -> actions.onBack()
                    SettingsBackTarget.Hub -> category = null
                }
            },
        )
        if (category == null) {
            SettingsHub(
                onCategory = { category = it },
                onAbout = actions.onAboutOpen,
                onClose = actions.onBack,
                modifier = Modifier.padding(SpacingMd),
            )
        } else {
            SettingsCategoryPane(
                category = category!!,
                state = state,
                actions = actions,
                onBack = { category = null },
                modifier = Modifier.padding(SpacingMd),
            )
        }
    }
}
