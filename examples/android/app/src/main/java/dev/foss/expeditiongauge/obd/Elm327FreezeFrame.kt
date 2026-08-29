package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.freezeframe.FreezeFrame
import dev.foss.expeditiongauge.freezeframe.FreezeFrameSnapshot
import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object Elm327FreezeFrame {
    fun request(reader: BufferedReader, writer: OutputStreamWriter): FreezeFrameSnapshot? {
        val dtcRaw = Elm327Io.sendCommand(reader, writer, "0202", timeoutMs = 8_000L)
            ?: return null
        val dtc = FreezeFrame.parseDtc(dtcRaw) ?: return null
        val snap = FreezeFrameSnapshot(
            dtc = dtc,
            rpm = pid(reader, writer, "020C", FreezeFrame::parseRpm),
            speedKmh = pid(reader, writer, "020D", FreezeFrame::parseSpeedKmh),
            throttlePct = pid(reader, writer, "0211") { FreezeFrame.parsePct(it, "4211") },
            loadPct = pid(reader, writer, "0204") { FreezeFrame.parsePct(it, "4204") },
        )
        return snap.takeIf { FreezeFrame.summary(it) != null }
    }

    private fun pid(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        cmd: String,
        parse: (String?) -> Float?,
    ): Float? {
        val raw = Elm327Io.sendCommand(reader, writer, cmd, timeoutMs = 4_000L) ?: return null
        return parse(raw)
    }
}
