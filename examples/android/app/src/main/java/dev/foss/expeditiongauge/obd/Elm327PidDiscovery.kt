package dev.foss.expeditiongauge.obd

import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object Elm327PidDiscovery {
    fun request(reader: BufferedReader, writer: OutputStreamWriter): Set<Int> {
        val ranges = listOf(
            Triple("0100", "4100", 0x01),
            Triple("0120", "4120", 0x21),
            Triple("0140", "4140", 0x41),
        )
        val out = linkedSetOf<Int>()
        for ((cmd, header, first) in ranges) {
            val raw = Elm327Protocol.queryPid(reader, writer, cmd) ?: continue
            out += ObdPidSupport.parseBitmap(raw, header, first)
        }
        return out
    }
}
