package dev.foss.expeditiongauge.live

import java.util.UUID
import kotlin.random.Random

data class LivePairingSession(
    val sessionId: String,
    val code: String,
    val signalWss: String,
    val qrPayload: String,
)

class LivePairingManager(
    private val defaultSignalWss: String = "wss://signaling.example.invalid/live",
) {
    fun createSession(): LivePairingSession {
        val sessionId = UUID.randomUUID().toString()
        val code = Random.nextInt(100_000, 999_999).toString()
        val qrPayload = "expeditiongauge://live?v=1&sessionId=$sessionId&code=$code&signalWss=$defaultSignalWss"
        return LivePairingSession(
            sessionId = sessionId,
            code = code,
            signalWss = defaultSignalWss,
            qrPayload = qrPayload,
        )
    }
}
