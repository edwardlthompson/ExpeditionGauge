package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.wifielm.WifiElmEndpoint
import java.net.InetSocketAddress
import java.net.Socket

internal object ObdTcp {
    fun open(endpoint: WifiElmEndpoint, timeoutMs: Int = 8_000): ObdLink {
        val sock = Socket()
        sock.tcpNoDelay = true
        sock.connect(InetSocketAddress(endpoint.host, endpoint.port), timeoutMs)
        return ObdLink(sock.inputStream, sock.outputStream) { sock.close() }
    }
}
