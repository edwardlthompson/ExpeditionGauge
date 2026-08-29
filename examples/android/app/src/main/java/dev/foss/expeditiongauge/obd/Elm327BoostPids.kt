package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.boostpids.BoostPidSnapshot
import dev.foss.expeditiongauge.boostpids.BoostPids
import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object Elm327BoostPids {
    fun request(reader: BufferedReader, writer: OutputStreamWriter): BoostPidSnapshot? {
        val map = pid(reader, writer, "010B", BoostPids::parseMapKpa)
        val baro = pid(reader, writer, "0133", BoostPids::parseBaroKpa)
        val afr = pid(reader, writer, "0134", BoostPids::parseAfr)
        val snap = BoostPidSnapshot(
            mapKpa = map,
            afr = afr,
            boostKpa = BoostPids.boostKpa(map, baro),
        )
        return snap.takeIf { BoostPids.line(it) != null }
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
