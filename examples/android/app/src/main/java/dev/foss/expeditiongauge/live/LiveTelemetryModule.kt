package dev.foss.expeditiongauge.live

import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.livemultireceiver.LiveMultiReceiver
import dev.foss.expeditiongauge.phoneimulive.PhoneImuLive
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

/**
 * Feature entry point for Live Telemetry (Sprint 19).
 * No network code runs until [FeatureFlags.liveTelemetryEnabled] is true.
 */
class LiveTelemetryModule(
    private val telemetryBus: TelemetryBus,
) {
    val webSocketClient = LiveWebSocketClient()
    val pairingManager = LivePairingManager()
    val sender = LiveTelemetrySender(telemetryBus, webSocketClient)
    val receiver = LiveTelemetryReceiver(webSocketClient)

    val receiverCount: StateFlow<Int> = webSocketClient.receiverCount

    fun pitRoomLabel(): String = LiveMultiReceiver.label(receiverCount.value)

    fun applyPhoneImu(payload: String, dto: LiveSampleDto): LiveSampleDto =
        PhoneImuLive.decode(payload)?.let { PhoneImuLive.merge(dto, it) } ?: dto

    fun isEnabled(): Boolean = FeatureFlags.liveTelemetryEnabled

    fun startSender(scope: CoroutineScope, session: LivePairingSession) {
        if (!isEnabled()) return
        sender.subscribe(scope)
        sender.startSession(scope, session)
    }

    fun stopSender() {
        sender.stopSession()
    }

    fun joinReceiver(scope: CoroutineScope, sessionId: String, code: String, signalWss: String) {
        if (!isEnabled()) return
        receiver.joinSession(scope, sessionId, code, signalWss)
    }

    fun stopReceiver() {
        receiver.disconnect()
    }
}
