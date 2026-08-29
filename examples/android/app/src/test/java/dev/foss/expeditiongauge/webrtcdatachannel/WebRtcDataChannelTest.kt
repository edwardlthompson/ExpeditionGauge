package dev.foss.expeditiongauge.webrtcdatachannel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WebRtcDataChannelTest {
    @Test
    fun framesAndOpensChannel() {
        val framed = WebRtcDataChannel.wrap("{\"t\":1}")
        assertTrue(framed.startsWith("dc1|"))
        assertEquals("{\"t\":1}", WebRtcDataChannel.unwrap(framed))
        assertEquals("plain", WebRtcDataChannel.unwrap("plain"))
        assertFalse(WebRtcDataChannel.canSend(WebRtcDataChannel.State.New))
        assertEquals(
            WebRtcDataChannel.State.Open,
            WebRtcDataChannel.afterOffer(WebRtcDataChannel.afterOffer(WebRtcDataChannel.State.New)),
        )
    }
}
