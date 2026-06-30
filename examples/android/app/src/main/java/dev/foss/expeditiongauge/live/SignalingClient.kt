package dev.foss.expeditiongauge.live

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class SignalingEvent {
    data class Connected(val sessionId: String) : SignalingEvent()
    data class Message(val payload: String) : SignalingEvent()
    data class Error(val message: String) : SignalingEvent()
    data object Disconnected : SignalingEvent()
}

/**
 * WebSocket signaling stub — relays SDP/ICE only (ADR-0006).
 * Replace with OkHttp WebSocket when FOSS stack audited.
 */
interface SignalingClient {
    val events: SharedFlow<SignalingEvent>
    suspend fun connect(sessionId: String, code: String, wssUrl: String)
    suspend fun sendSdp(type: String, sdp: String)
    suspend fun sendIce(candidate: String)
    suspend fun disconnect()
}

class SignalingClientStub : SignalingClient {
    private val _events = MutableSharedFlow<SignalingEvent>(extraBufferCapacity = 8)
    override val events: SharedFlow<SignalingEvent> = _events.asSharedFlow()

    private var connected = false

    override suspend fun connect(sessionId: String, code: String, wssUrl: String) {
        connected = true
        _events.emit(SignalingEvent.Connected(sessionId))
        // Stub: simulate offer/answer handshake placeholder
        _events.emit(SignalingEvent.Message("{\"type\":\"stub-ready\",\"code\":\"$code\"}"))
    }

    override suspend fun sendSdp(type: String, sdp: String) {
        if (!connected) return
        _events.emit(SignalingEvent.Message("{\"type\":\"$type\",\"sdp\":\"stub\"}"))
    }

    override suspend fun sendIce(candidate: String) {
        if (!connected) return
        _events.emit(SignalingEvent.Message("{\"type\":\"ice\",\"candidate\":\"stub\"}"))
    }

    override suspend fun disconnect() {
        connected = false
        _events.emit(SignalingEvent.Disconnected)
    }
}
