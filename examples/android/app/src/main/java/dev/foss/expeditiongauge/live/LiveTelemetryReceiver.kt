package dev.foss.expeditiongauge.live

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class LiveReceiverState {
    Idle,
    Connecting,
    Connected,
}

/**
 * In-app receiver WebRTC stub — parses live JSON into gauge-ready state.
 */
class LiveTelemetryReceiver(
    private val signalingClient: SignalingClient = SignalingClientStub(),
    private val transport: LiveTelemetryTransport = StubLiveTelemetryTransport(),
) {
    private val _state = MutableStateFlow(LiveReceiverState.Idle)
    val state: StateFlow<LiveReceiverState> = _state.asStateFlow()

    private val _latestJson = MutableStateFlow<String?>(null)
    val latestJson: StateFlow<String?> = _latestJson.asStateFlow()

    suspend fun joinSession(sessionId: String, code: String, signalWss: String) {
        _state.value = LiveReceiverState.Connecting
        signalingClient.connect(sessionId, code, signalWss)
        transport.connect(sessionId, code)
        _state.value = LiveReceiverState.Connected
    }

    fun onMetricReceived(json: String) {
        _latestJson.value = json
    }

    suspend fun disconnect() {
        transport.disconnect()
        signalingClient.disconnect()
        _state.value = LiveReceiverState.Idle
        _latestJson.value = null
    }
}
