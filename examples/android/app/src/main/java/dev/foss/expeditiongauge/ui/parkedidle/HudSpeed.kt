package dev.foss.expeditiongauge.ui.parkedidle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.ExpeditionGaugeApplication
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun rememberHudSpeedMps(): Float? {
    val context = LocalContext.current
    val snapshots = remember(context) {
        (context.applicationContext as? ExpeditionGaugeApplication)
            ?.services
            ?.telemetryBus
            ?.snapshots
            ?: emptyFlow()
    }
    val snap by snapshots.collectAsStateWithLifecycle(initialValue = null)
    return snap?.speedMps
}
