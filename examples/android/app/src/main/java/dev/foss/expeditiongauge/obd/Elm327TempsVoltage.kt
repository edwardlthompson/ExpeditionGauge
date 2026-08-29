package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.obdtemps.ObdTempsVoltage
import dev.foss.expeditiongauge.obdtemps.ObdTempsVoltageSnapshot
import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object Elm327TempsVoltage {
    fun request(reader: BufferedReader, writer: OutputStreamWriter): ObdTempsVoltageSnapshot? {
        val snap = ObdTempsVoltageSnapshot(
            coolantC = pid(reader, writer, "0105", ObdTempsVoltage::parseCoolantC),
            oilC = pid(reader, writer, "015C", ObdTempsVoltage::parseOilC),
            voltage = pid(reader, writer, "0142", ObdTempsVoltage::parseVoltage),
        )
        return snap.takeIf { ObdTempsVoltage.line(it) != null }
    }

    private fun pid(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        cmd: String,
        parse: (String?) -> Float?,
    ): Float? {
        val raw = Elm327Io.sendCommand(reader, writer, cmd, timeoutMs = 3_000L) ?: return null
        return parse(raw)
    }
}
