package dev.foss.expeditiongauge.webrtcdatachannel

/** FOSS Data Channel framing until a PeerConnection stack is audited (ADR-0006). */
object WebRtcDataChannel {
    enum class State { New, Connecting, Open, Closed }

    const val PREFIX = "dc1|"

    fun wrap(payload: String): String = PREFIX + payload

    fun unwrap(framed: String): String? {
        val text = framed.trim()
        if (text.isEmpty()) return null
        return if (text.startsWith(PREFIX)) text.removePrefix(PREFIX) else text
    }

    fun canSend(state: State): Boolean = state == State.Open

    fun afterOffer(state: State): State = when (state) {
        State.New -> State.Connecting
        State.Connecting -> State.Open
        else -> state
    }
}
