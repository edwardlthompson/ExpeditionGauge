package dev.foss.expeditiongauge.live

import dev.foss.expeditiongauge.FeatureFlags
import dev.foss.expeditiongauge.telemetry.TelemetryBus
import dev.foss.expeditiongauge.webrtcdatachannel.WebRtcDataChannel
import dev.foss.expeditiongauge.telemetry.TelemetrySnapshot
import dev.foss.expeditiongauge.telemetry.TpmsSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class LiveSenderState {
    Idle,
    Pairing,
    Live,
}

class LiveTelemetrySender(
    private val telemetryBus: TelemetryBus,
    private val webSocketClient: LiveWebSocketClient,
    private val encoder: LiveTelemetryEncoder = LiveTelemetryEncoder(),
) {
    var state: LiveSenderState = LiveSenderState.Idle
        private set

    val connectedReceivers: Int
        get() = webSocketClient.receiverCount.value

    private var collectJob: Job? = null

    fun startSession(scope: CoroutineScope, session: LivePairingSession) {
        state = LiveSenderState.Pairing
        webSocketClient.connect(session.sessionId, session.code, session.signalWss, "sender")
        state = LiveSenderState.Live
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

    fun onSnapshot(snapshot: TelemetrySnapshot) {
        val tpmsJson = tpmsToJson(snapshot.tpms)
        val payload = encoder.encodeIfChanged(snapshot, tpmsJson) ?: return
        webSocketClient.sendMetric(WebRtcDataChannel.wrap(payload))
    }

    fun stopSession() {
        collectJob?.cancel()
        collectJob = null
        webSocketClient.disconnect()
        encoder.reset()
        state = LiveSenderState.Idle
    }

    private fun tpmsToJson(tpms: TpmsSnapshot?): String? {
        if (!FeatureFlags.tpmsEnabled || tpms == null) return null
        fun corner(c: dev.foss.expeditiongauge.telemetry.TpmsCornerReading) = JSONObject().apply {
            c.pressureKpa?.let { put("kpa", it.toDouble()) }
            c.tempC?.let { put("tempC", it.toDouble()) }
        }
        return JSONObject()
            .put("fl", corner(tpms.frontLeft))
            .put("fr", corner(tpms.frontRight))
            .put("rl", corner(tpms.rearLeft))
            .put("rr", corner(tpms.rearRight))
            .toString()
    }
}
