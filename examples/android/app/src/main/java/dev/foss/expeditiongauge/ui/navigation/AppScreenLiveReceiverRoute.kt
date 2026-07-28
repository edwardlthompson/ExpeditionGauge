package dev.foss.expeditiongauge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.foss.expeditiongauge.ExpeditionGaugeServices
import dev.foss.expeditiongauge.settings.SpeedUnit
import dev.foss.expeditiongauge.ui.AppScreen
import dev.foss.expeditiongauge.ui.live.LiveReceiverScreen
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun AppScreenLiveReceiverRoute(
    scope: CoroutineScope,
    services: ExpeditionGaugeServices,
    liveSignalWssUrl: String,
    speedUnit: SpeedUnit,
    onScreenChange: (AppScreen) -> Unit,
) {
    var sessionId by remember { mutableStateOf("") }
    var sessionCode by remember { mutableStateOf("") }
    var signalWss by remember { mutableStateOf(liveSignalWssUrl) }
    LaunchedEffect(liveSignalWssUrl) { signalWss = liveSignalWssUrl }
    val latestSample by services.liveTelemetryModule.receiver.latestSample
        .collectAsStateWithLifecycle(initialValue = null)
    val receiverState by services.liveTelemetryModule.receiver.state
        .collectAsStateWithLifecycle(initialValue = dev.foss.expeditiongauge.live.LiveReceiverState.Idle)
    LiveReceiverScreen(
        latestSample = latestSample,
        sessionId = sessionId,
        onSessionIdChange = { sessionId = it },
        sessionCode = sessionCode,
        onSessionCodeChange = { sessionCode = it },
        signalWss = signalWss,
        onSignalWssChange = { signalWss = it },
        onJoin = {
            services.liveTelemetryModule.joinReceiver(scope, sessionId, sessionCode, signalWss)
        },
        onDisconnect = { services.liveTelemetryModule.stopReceiver() },
        isConnected = receiverState == dev.foss.expeditiongauge.live.LiveReceiverState.Connected,
        onBack = {
            services.liveTelemetryModule.stopReceiver()
            onScreenChange(AppScreen.Settings)
        },
        speedUnit = speedUnit,
    )
}
