package dev.foss.expeditiongauge.ui.appshortcuts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.foss.expeditiongauge.appshortcuts.AppShortcuts

@Composable
fun AppShortcutEffect(onLibrary: () -> Unit) {
    LaunchedEffect(Unit) {
        if (AppShortcuts.consume() == AppShortcuts.ACTION_LIBRARY) onLibrary()
    }
}
