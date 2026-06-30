package dev.foss.expeditiongauge.live

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LivePairingManagerTest {
    private val manager = LivePairingManager(defaultSignalWss = "ws://test/live")

    @Test
    fun createSession_generatesCodeAndQrPayload() {
        val session = manager.createSession()
        assertEquals(36, session.sessionId.length)
        assertEquals(6, session.code.length)
        assertTrue(session.qrPayload.contains("sessionId=${session.sessionId}"))
        assertTrue(session.qrPayload.contains("code=${session.code}"))
        assertEquals("ws://test/live", session.signalWss)
    }

    @Test
    fun createSession_usesCustomSignalUrl() {
        val session = manager.createSession("wss://example.com/live")
        assertEquals("wss://example.com/live", session.signalWss)
    }
}
