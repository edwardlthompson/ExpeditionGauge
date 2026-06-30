package dev.foss.expeditiongauge.live

import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class LiveSenderState {
    Idle,
    Pairing,
    Live,
}

/**
 * WebRTC Data Channel sender stub — subscribes to TelemetryBus without forking fusion.
 */
class LiveTelemetrySender(
    private val telemetryBus: TelemetryBus,
    private val encoder: LiveTelemetryEncoder = LiveTelemetryEncoder(),
    private val transport: LiveTelemetryTransport = StubLiveTelemetryTransport(),
    private val signalingClient: SignalingClient = SignalingClientStub(),
) {
    var state: LiveSenderState = LiveSenderState.Idle
        private set

    var connectedReceivers: Int = 0
        private set

    private var collectJob: Job? = null

    suspend fun startSession(session: LivePairingSession) {
        state = LiveSenderState.Pairing
        signalingClient.connect(session.sessionId, session.code, session.signalWss)
        transport.connect(session.sessionId, session.code)
        state = LiveSenderState.Live
        connectedReceivers = 1
    }

    fun subscribe(scope: CoroutineScope) {
        collectJob?.cancel()
        collectJob = scope.launch {
            telemetryBus.snapshots.collectLatest { snapshot ->
                if (state != LiveSenderState.Live) return@collectLatest
                onSnapshot(snapshot)
            }
        }
    }

    suspend fun onSnapshot(snapshot: TelemetrySnapshot) {
        val payload = encoder.encodeIfChanged(snapshot) ?: return
        transport.send(payload)
    }

    suspend fun stopSession() {
        collectJob?.cancel()
        collectJob = null
        transport.disconnect()
        signalingClient.disconnect()
        encoder.reset()
        state = LiveSenderState.Idle
        connectedReceivers = 0
    }
}
