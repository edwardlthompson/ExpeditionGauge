package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import java.io.BufferedReader
import java.io.OutputStreamWriter

object Elm327Protocol {
    fun init(sock: BluetoothSocket) {
        val writer = OutputStreamWriter(sock.outputStream)
        val reader = BufferedReader(sock.inputStream.reader())
        // ATZ resets; ATSP0 may search protocols — allow longer waits than PID polls.
        Elm327Io.sendCommand(reader, writer, "ATZ", timeoutMs = 8_000L)
        Elm327Io.sendCommand(reader, writer, "ATE0", timeoutMs = 3_000L)
        Elm327Io.sendCommand(reader, writer, "ATL0", timeoutMs = 3_000L)
        Elm327Io.sendCommand(reader, writer, "ATSP0", timeoutMs = 8_000L)
    }

    fun queryPid(reader: BufferedReader, writer: OutputStreamWriter, pid: String): String? {
        val raw = Elm327Io.sendCommand(reader, writer, pid, timeoutMs = 3_000L)
        return raw?.filter { it.isLetterOrDigit() }?.uppercase()
    }

    fun readUntilPrompt(reader: BufferedReader): String? =
        Elm327Io.readUntilPrompt(reader)

    fun parseRpm(response: String): Float? {
        val idx = response.indexOf("410C")
        if (idx < 0 || idx + 8 > response.length) return null
        val a = response.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b = response.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        return (a * 256 + b) / 4f
    }

    /**
     * Vehicle speed (PID 010D) — one byte, km/h.
     * Must anchor on `410D` so trailing junk / other PIDs are not treated as speed
     * (a prior bug used [takeLast] and could surface values like 147 km/h as bogus speed).
     */
    fun parseVehicleSpeedKmh(response: String): Float? = parsePidDataByte(response, "410D")

    /** Throttle/load-style single-byte PIDs (`4111`, `4104`, …). */
    fun parsePidDataByte(response: String, header: String): Float? {
        val idx = response.indexOf(header)
        if (idx < 0 || idx + header.length + 2 > response.length) return null
        val hex = response.substring(idx + header.length, idx + header.length + 2)
        return hex.toIntOrNull(16)?.toFloat()
    }

    @Deprecated("Use parseVehicleSpeedKmh / parsePidDataByte — takeLast mis-parses noisy ELM buffers")
    fun parseSingleByte(response: String): Float {
        // Prefer anchored 410D when present (keeps older call sites safer).
        parseVehicleSpeedKmh(response)?.let { return it }
        val hex = response.takeLast(2)
        return hex.toIntOrNull(16)?.toFloat() ?: 0f
    }

    fun parseVoltage(response: String): Float? {
        val idx = response.indexOf("4142")
        if (idx < 0 || idx + 8 > response.length) return null
        val a = response.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b = response.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        return (a * 256 + b) / 1000f
    }

    /**
     * One-shot Mode 03 (stored DTCs). Sends `03`, returns SAE codes (`Pxxxx`…).
     * `NO DATA` / timeout / empty → empty list (does not throw).
     */
    fun requestStoredDtcs(reader: BufferedReader, writer: OutputStreamWriter): List<String> {
        val raw = queryPid(reader, writer, "03") ?: return emptyList()
        return parseStoredDtcs(raw)
    }

    /** Mode 07 pending/unconfirmed DTCs (service `47`). */
    fun requestPendingDtcs(reader: BufferedReader, writer: OutputStreamWriter): List<String> {
        val raw = queryPid(reader, writer, "07") ?: return emptyList()
        return parseServiceDtcs(raw, "47")
    }

    /**
     * Parse Mode 03 / service `43` frames into distinct DTC codes.
     * Strips ELM line prefixes (`0:`), CAN headers, and `00 00` padding.
     */
    fun parseStoredDtcs(raw: String): List<String> = parseServiceDtcs(raw, "43")

    fun parseServiceDtcs(raw: String, sidHex: String): List<String> {
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE") || upper.contains("ERROR")) {
            return emptyList()
        }
        val hex = normalizeElmHex(raw)
        if (hex.isEmpty()) return emptyList()
        val sid = sidHex.uppercase()
        val idx = indexOfEvenHex(hex, sid)
        if (idx < 0) return emptyList()
        var i = idx + sid.length
        val codes = ArrayList<String>(8)
        while (i + 4 <= hex.length) {
            val b1 = hex.substring(i, i + 2).toIntOrNull(16) ?: break
            val b2 = hex.substring(i + 2, i + 4).toIntOrNull(16) ?: break
            i += 4
            if (b1 == 0 && b2 == 0) continue
            codes.add(decodeDtcBytes(b1, b2))
        }
        return codes.distinct()
    }

    /** SID match on even nibble pairs so `43` is not found inside `A43F`. */
    internal fun indexOfEvenHex(hex: String, token: String): Int {
        var i = 0
        while (i + token.length <= hex.length) {
            if (hex.regionMatches(i, token, 0, token.length)) return i
            i += 2
        }
        return -1
    }

    /** Drop `N:` multi-frame prefixes; keep hex digits only. */
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
