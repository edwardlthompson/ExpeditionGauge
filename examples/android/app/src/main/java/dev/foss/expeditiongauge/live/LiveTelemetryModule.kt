package dev.foss.expeditiongauge.live

import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Feature entry point for Live Telemetry (Sprint 19).
 * No network code runs until [FeatureFlags.liveTelemetryEnabled] is true.
 */
class LiveTelemetryModule(
    private val telemetryBus: TelemetryBus,
) {
    val pairingManager = LivePairingManager()
    val sender = LiveTelemetrySender(telemetryBus)
    val receiver = LiveTelemetryReceiver()

    fun isEnabled(): Boolean = FeatureFlags.liveTelemetryEnabled

    fun startSender(scope: CoroutineScope, session: LivePairingSession) {
        if (!isEnabled()) return
        sender.subscribe(scope)
        scope.launch {
            sender.startSession(session)
        }
    }

    suspend fun stopSender() {
        sender.stopSession()
    }
}
