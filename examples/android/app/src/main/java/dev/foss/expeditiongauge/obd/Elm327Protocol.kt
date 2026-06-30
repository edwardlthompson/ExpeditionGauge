package dev.foss.expeditiongauge.obd

import android.bluetooth.BluetoothSocket
import java.io.BufferedReader
import java.io.OutputStreamWriter

object Elm327Protocol {
    fun init(sock: BluetoothSocket) {
        val writer = OutputStreamWriter(sock.outputStream)
        val reader = BufferedReader(sock.inputStream.reader())
        listOf("ATZ", "ATE0", "ATL0", "ATSP0").forEach { cmd ->
            writer.write("$cmd\r")
            writer.flush()
            readUntilPrompt(reader)
        }
    }

    fun queryPid(reader: BufferedReader, writer: OutputStreamWriter, pid: String): String? {
        writer.write("$pid\r")
        writer.flush()
        return readUntilPrompt(reader)?.filter { it.isLetterOrDigit() }?.uppercase()
    }

    fun readUntilPrompt(reader: BufferedReader): String? {
        val sb = StringBuilder()
        repeat(20) {
            if (!reader.ready()) return@repeat
            val c = reader.read().toChar()
            sb.append(c)
            if (c == '>') return sb.toString()
        }
        return sb.toString().ifBlank { null }
    }

    fun parseRpm(response: String): Float? {
        val idx = response.indexOf("410C")
        if (idx < 0 || idx + 8 > response.length) return null
        val a = response.substring(idx + 4, idx + 6).toIntOrNull(16) ?: return null
        val b = response.substring(idx + 6, idx + 8).toIntOrNull(16) ?: return null
        return (a * 256 + b) / 4f
    }

    fun parseSingleByte(response: String): Float {
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
}
