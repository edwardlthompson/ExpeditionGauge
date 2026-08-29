package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.vinlast6.VinLast6
import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object Elm327Vin {
    fun requestLast6(reader: BufferedReader, writer: OutputStreamWriter): String? {
        val raw = Elm327Io.sendCommand(reader, writer, "0902", timeoutMs = 8_000L) ?: return null
        return VinLast6.last6(VinLast6.parseVin(raw))
    }
}
