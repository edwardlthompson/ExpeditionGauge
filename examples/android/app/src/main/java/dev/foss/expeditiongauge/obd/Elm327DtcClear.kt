package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.dtcclear.DtcClear
import java.io.BufferedReader
import java.io.OutputStreamWriter

object Elm327DtcClear {
    fun request(reader: BufferedReader, writer: OutputStreamWriter): Boolean {
        val raw = Elm327Io.sendCommand(reader, writer, "04", timeoutMs = 8_000L) ?: return false
        return DtcClear.parseAck(raw)
    }
}
