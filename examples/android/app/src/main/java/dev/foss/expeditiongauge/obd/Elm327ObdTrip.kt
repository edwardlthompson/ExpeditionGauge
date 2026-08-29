package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.obdtrip.ObdTrip
import dev.foss.expeditiongauge.obdtrip.ObdTripSinceClear
import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object Elm327ObdTrip {
    fun request(reader: BufferedReader, writer: OutputStreamWriter): ObdTripSinceClear? {
        val snap = ObdTripSinceClear(
            distanceKm = pid(reader, writer, "0131", ObdTrip::parseDistanceKm),
            warmups = pid(reader, writer, "0130", ObdTrip::parseWarmups),
            timeMin = pid(reader, writer, "014E", ObdTrip::parseTimeMin),
        )
        return snap.takeIf { ObdTrip.line(it) != null }
    }

    private fun pid(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        cmd: String,
        parse: (String?) -> Int?,
    ): Int? {
        val raw = Elm327Io.sendCommand(reader, writer, cmd, timeoutMs = 4_000L) ?: return null
        return parse(raw)
    }
}
