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
    private val defaultSignalWss: String = DEFAULT_SIGNAL_WSS,
) {
    fun createSession(signalWss: String = defaultSignalWss): LivePairingSession {
        val sessionId = UUID.randomUUID().toString()
        val code = Random.nextInt(100_000, 999_999).toString()
        val qrPayload =
            "expeditiongauge://live?v=1&sessionId=$sessionId&code=$code&signalWss=${encode(signalWss)}"
        return LivePairingSession(
            sessionId = sessionId,
            code = code,
            signalWss = signalWss,
            qrPayload = qrPayload,
        )
    }

    companion object {
        const val DEFAULT_SIGNAL_WSS = "ws://127.0.0.1:8787/live"

        private fun encode(value: String): String =
            java.net.URLEncoder.encode(value, Charsets.UTF_8.name())
    }
}
