package dev.foss.expeditiongauge.live

/**
 * Extension point for alternate live transports (LAN-only UDP, etc.).
 */
interface LiveTelemetryTransport {
    val isConnected: Boolean
    suspend fun connect(sessionId: String, code: String)
    suspend fun send(payload: String)
    suspend fun disconnect()
}

class StubLiveTelemetryTransport : LiveTelemetryTransport {
    override var isConnected: Boolean = false
        private set

    override suspend fun connect(sessionId: String, code: String) {
        isConnected = true
    }

    override suspend fun send(payload: String) {
        // Stub — no network I/O until WebRTC wired
    }

    override suspend fun disconnect() {
        isConnected = false
    }
}
