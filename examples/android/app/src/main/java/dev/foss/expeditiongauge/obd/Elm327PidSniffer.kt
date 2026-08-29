package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.pidsniffer.PidSniffer
import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object Elm327PidSniffer {
    fun request(reader: BufferedReader, writer: OutputStreamWriter, command: String): String {
        val raw = Elm327Io.sendCommand(reader, writer, command, timeoutMs = 3_000L)
        return PidSniffer.sanitize(command, raw)
    }
}
