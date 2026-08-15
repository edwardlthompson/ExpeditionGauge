package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import java.io.BufferedReader
import java.io.OutputStreamWriter

object Elm327Protocol {
    /** Set by [Elm327Init] from ATDPN; null = heuristic framing. */
    @Volatile
    var canFraming: Boolean? = null

    fun init(sock: BluetoothSocket) {
        val writer = OutputStreamWriter(sock.outputStream)
        val reader = BufferedReader(sock.inputStream.reader())
        Elm327Init.run(reader, writer)
    }

    fun queryPid(reader: BufferedReader, writer: OutputStreamWriter, pid: String): String? {
        val raw = Elm327Io.sendCommand(reader, writer, pid, timeoutMs = 3_000L) ?: return null
        return Elm327DtcParse.stripNoise(raw)
            .filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }
            .uppercase()
            .ifBlank { null }
    }

    fun readUntilPrompt(reader: BufferedReader): String? =
        Elm327Io.readUntilPrompt(reader)

    fun parseRpm(response: String): Float? {
        val hex = response.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
        val idx = indexOfEvenHex(hex, "410C").takeIf { it >= 0 } ?: hex.indexOf("410C")
        if (idx < 0 || idx + 8 > hex.length) return null
        val a = hex.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b = hex.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        // SAE J1979: ((A*256)+B)/4. Some clones emit already-scaled tenths (~10×).
        val rpm = (a * 256 + b) / 4f
        return when {
            rpm <= 8_000f -> rpm
            rpm <= 20_000f -> rpm / 10f
            else -> null
        }
    }

    /**
     * Vehicle speed (PID 010D) — one byte, km/h.
     * Must anchor on `410D` so trailing junk / other PIDs are not treated as speed.
     */
    fun parseVehicleSpeedKmh(response: String): Float? = parsePidDataByte(response, "410D")

    /** Throttle/load-style single-byte PIDs (`4111`, `4104`, …). */
    fun parsePidDataByte(response: String, header: String): Float? {
        val idx = response.indexOf(header)
        if (idx < 0 || idx + header.length + 2 > response.length) return null
        val hex = response.substring(idx + header.length, idx + header.length + 2)
        return hex.toIntOrNull(16)?.toFloat()
    }

    fun parseVoltage(response: String): Float? {
        val idx = response.indexOf("4142")
        if (idx < 0 || idx + 8 > response.length) return null
        val a = response.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b = response.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        return (a * 256 + b) / 1000f
    }

    fun requestStoredDtcs(reader: BufferedReader, writer: OutputStreamWriter): List<String> {
        val raw = Elm327Io.sendCommand(reader, writer, "03", timeoutMs = 12_000L) ?: return emptyList()
        return parseStoredDtcs(raw)
    }

    fun requestPendingDtcs(reader: BufferedReader, writer: OutputStreamWriter): List<String> {
        val raw = Elm327Io.sendCommand(reader, writer, "07", timeoutMs = 12_000L) ?: return emptyList()
        return parseServiceDtcs(raw, "47")
    }

    fun parseStoredDtcs(raw: String, canFraming: Boolean? = this.canFraming): List<String> =
        parseServiceDtcs(raw, "43", canFraming)

    fun parseServiceDtcs(
        raw: String,
        sidHex: String,
        canFraming: Boolean? = this.canFraming,
    ): List<String> = Elm327DtcParse.parseServiceDtcs(raw, sidHex, canFraming)

    internal fun indexOfEvenHex(hex: String, token: String): Int {
        var i = 0
        while (i + token.length <= hex.length) {
            if (hex.regionMatches(i, token, 0, token.length)) return i
            i += 2
        }
        return -1
    }

    fun normalizeElmHex(raw: String): String =
        raw.lineSequence()
            .map { line ->
                val payload = if (':' in line) line.substringAfterLast(':') else line
                payload.filter { it.isLetterOrDigit() }.uppercase()
            }
            .joinToString("")

    fun decodeDtcBytes(b1: Int, b2: Int): String {
        val sys = (b1 shr 6) and 0x03
        val letter = when (sys) {
            0 -> 'P'
            1 -> 'C'
            2 -> 'B'
            else -> 'U'
        }
        val d1 = (b1 shr 4) and 0x03
        val d2 = b1 and 0x0F
        val d3 = (b2 shr 4) and 0x0F
        val d4 = b2 and 0x0F
        return "$letter$d1${d2.toString(16)}${d3.toString(16)}${d4.toString(16)}".uppercase()
    }
}
