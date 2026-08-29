package dev.foss.expeditiongauge.live

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import dev.foss.expeditiongauge.liveencrypt.LiveEncrypt
import dev.foss.expeditiongauge.livereceiverrecord.LiveReceiverRecord
import dev.foss.expeditiongauge.webrtcdatachannel.WebRtcDataChannel
import org.json.JSONObject

enum class LiveReceiverState {
    Idle,
    Connecting,
    Connected,
}

class LiveTelemetryReceiver(
    private val webSocketClient: LiveWebSocketClient,
) {
    private val _state = MutableStateFlow(LiveReceiverState.Idle)
    val state: StateFlow<LiveReceiverState> = _state.asStateFlow()

    private val _latestSample = MutableStateFlow<LiveSampleDto?>(null)
    val latestSample: StateFlow<LiveSampleDto?> = _latestSample.asStateFlow()

    private var metricsJob: Job? = null

    fun joinSession(scope: CoroutineScope, sessionId: String, code: String, signalWss: String) {
        _state.value = LiveReceiverState.Connecting
        webSocketClient.connect(sessionId, code, signalWss, "receiver")
        metricsJob?.cancel()
        metricsJob = scope.launch {
            webSocketClient.metrics.collectLatest { json -> onMetricReceived(json) }
        }
        _state.value = LiveReceiverState.Connected
    }

    fun onMetricReceived(json: String) {
        val raw = WebRtcDataChannel.unwrap(json) ?: return
        val plain = LiveEncrypt.open(raw) ?: return
        val obj = runCatching { JSONObject(plain) }.getOrNull() ?: return
        val dto = LiveSampleDto(
            timestampMs = obj.optLong("t"),
            speedMps = obj.optDouble("speed").toFloat(),
            latG = obj.optDouble("latG").toFloat(),
            betaDeg = if (obj.has("beta")) obj.optDouble("beta").toFloat() else null,
            pitchDeg = obj.optDouble("pitch").toFloat(),
            rollDeg = obj.optDouble("roll").toFloat(),
            headingDeg = obj.optDouble("hdg").toFloat(),
            tpmsJson = obj.optJSONObject("tpms")?.toString(),
        )
        _latestSample.value = dto
        LiveReceiverRecord.remember(dto)
    }

    fun disconnect() {
        metricsJob?.cancel()
        metricsJob = null
        webSocketClient.disconnect()
        _state.value = LiveReceiverState.Idle
        _latestSample.value = null
        LiveReceiverRecord.clear()
    }
}
