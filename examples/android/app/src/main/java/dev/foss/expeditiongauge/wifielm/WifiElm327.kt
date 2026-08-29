package dev.foss.expeditiongauge.wifielm

data class WifiElmEndpoint(val host: String, val port: Int)

object WifiElm327 {
    const val PREFIX = "tcp:"
    const val DEFAULT_HOST = "192.168.0.10"
    const val DEFAULT_PORT = 35_000
    const val DEFAULT_DISPLAY = "$DEFAULT_HOST:$DEFAULT_PORT"

    fun isWifi(raw: String): Boolean = raw.startsWith(PREFIX, ignoreCase = true)

    fun encode(host: String, port: Int = DEFAULT_PORT): String = "$PREFIX$host:$port"

    fun display(raw: String?): String = parse(raw)?.let { "${it.host}:${it.port}" } ?: DEFAULT_DISPLAY

    fun parse(raw: String?): WifiElmEndpoint? {
        if (raw.isNullOrBlank() || !isWifi(raw)) return null
        val body = raw.substring(PREFIX.length)
        val split = body.lastIndexOf(':')
        val host: String
        val port: Int
        if (split > 0) {
            host = body.substring(0, split).trim()
            port = body.substring(split + 1).trim().toIntOrNull() ?: return null
        } else {
            host = body.trim()
            port = DEFAULT_PORT
        }
        if (port !in 1..65_535 || !privateHost(host)) return null
        return WifiElmEndpoint(host, port)
    }

    fun parseDisplay(display: String): WifiElmEndpoint? {
        val trimmed = display.trim()
        if (trimmed.isEmpty()) return parse(encode(DEFAULT_HOST, DEFAULT_PORT))
        return parse(if (isWifi(trimmed)) trimmed else "$PREFIX$trimmed")
    }

    internal fun privateHost(host: String): Boolean {
        if (host.equals("localhost", ignoreCase = true) || host == "127.0.0.1") return true
        val p = host.split('.')
        if (p.size != 4) return false
        val oct = p.map { it.toIntOrNull() ?: return false }
        if (oct.any { it !in 0..255 }) return false
        val a = oct[0]
        val b = oct[1]
        return a == 10 || (a == 192 && b == 168) || (a == 172 && b in 16..31) ||
            (a == 169 && b == 254)
    }
}
