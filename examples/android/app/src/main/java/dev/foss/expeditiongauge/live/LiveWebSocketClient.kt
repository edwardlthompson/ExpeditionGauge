package dev.foss.expeditiongauge.live

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * OkHttp WebSocket client for signaling rooms and stub metric relay (ADR-0006).
 */
class LiveWebSocketClient(
    client: OkHttpClient? = null,
) {
    private val okHttp = client ?: OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var sessionId: String? = null

    private val _receiverCount = MutableStateFlow(0)
    val receiverCount: StateFlow<Int> = _receiverCount.asStateFlow()

    private val _metrics = MutableSharedFlow<String>(extraBufferCapacity = 128)
    val metrics: SharedFlow<String> = _metrics.asSharedFlow()

    private val _events = MutableSharedFlow<SignalingEvent>(extraBufferCapacity = 32)
    val events: SharedFlow<SignalingEvent> = _events.asSharedFlow()

    val isConnected: Boolean get() = webSocket != null

    fun connect(sessionId: String, code: String, wssUrl: String, role: String) {
        disconnect()
        this.sessionId = sessionId
        val request = Request.Builder().url(wssUrl).build()
        webSocket = okHttp.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    sendJson(
                        JSONObject()
                            .put("type", "join")
                            .put("role", role)
                            .put("sessionId", sessionId)
                            .put("code", code),
                    )
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    handleMessage(text)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    _events.tryEmit(SignalingEvent.Error(t.message ?: "websocket failure"))
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    _events.tryEmit(SignalingEvent.Disconnected)
                }
            },
        )
    }

    fun sendMetric(payload: String) {
        sendJson(JSONObject().put("type", "metric").put("payload", payload))
    }

    fun sendSdp(type: String, sdp: String) {
        sendJson(JSONObject().put("type", "sdp").put("sdpType", type).put("sdp", sdp))
    }

    fun sendIce(candidate: String) {
        sendJson(JSONObject().put("type", "ice").put("candidate", candidate))
    }

    fun disconnect() {
        webSocket?.close(1000, "bye")
        webSocket = null
        sessionId = null
        _receiverCount.value = 0
    }

    private fun sendJson(obj: JSONObject) {
        webSocket?.send(obj.toString())
    }

    private fun handleMessage(text: String) {
        val msg = runCatching { JSONObject(text) }.getOrNull() ?: return
        when (msg.optString("type")) {
            "joined" -> {
                _receiverCount.value = msg.optInt("receiverCount", 0)
                sessionId?.let { _events.tryEmit(SignalingEvent.Connected(it)) }
            }
            "metric" -> {
                val payload = msg.optString("payload", "")
                if (payload.isNotEmpty()) _metrics.tryEmit(payload)
            }
            "sdp" -> _events.tryEmit(
                SignalingEvent.Message(
                    """{"type":"sdp","sdpType":"${msg.optString("sdpType")}"}""",
                ),
            )
            "ice" -> _events.tryEmit(SignalingEvent.Message("""{"type":"ice"}"""))
            "error" -> _events.tryEmit(SignalingEvent.Error(msg.optString("message", "error")))
        }
    }
}
