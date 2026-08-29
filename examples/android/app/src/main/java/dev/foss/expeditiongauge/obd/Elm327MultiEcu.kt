package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.multiecu.MultiEcuHeaders
import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object Elm327MultiEcu {
    fun request(reader: BufferedReader, writer: OutputStreamWriter): List<String>? {
        val found = mutableListOf<String>()
        try {
            for (header in MultiEcuHeaders.entries) {
                Elm327Io.sendCommand(reader, writer, MultiEcuHeaders.atsh(header.id), timeoutMs = 1_500L)
                val raw = Elm327Protocol.queryPid(reader, writer, "0100", timeoutMs = 2_000L)
                if (MultiEcuHeaders.present(raw)) found += header.id
            }
        } finally {
            Elm327Io.sendCommand(reader, writer, MultiEcuHeaders.atsh(MultiEcuHeaders.FUNCTIONAL), timeoutMs = 1_500L)
        }
        return found.takeIf { MultiEcuHeaders.line(it) != null }
    }
}
