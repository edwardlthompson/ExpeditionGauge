package dev.foss.expeditiongauge.obd

import java.io.BufferedReader
import java.io.OutputStreamWriter

/** Mode 01 PID 01 — MIL + stored DTC count (bits 0–6). */
data class MonitorStatus(val milOn: Boolean, val storedDtcCount: Int)

/** Parse / request monitor status used to gate Mode 03 traffic. */
internal object ObdMonitorStatus {
    fun request(reader: BufferedReader, writer: OutputStreamWriter): MonitorStatus? {
        val raw = Elm327Protocol.queryPid(reader, writer, "0101") ?: return null
        return parse(raw)
    }

    fun parse(raw: String): MonitorStatus? {
        val hex = Elm327Protocol.normalizeElmHex(raw)
        var i = 0
        var total = 0
        var mil = false
        var found = false
        while (true) {
            val idx = hex.indexOf("4101", i)
            if (idx < 0 || idx + 6 > hex.length) break
            val a = hex.substring(idx + 4, idx + 6).toIntOrNull(16) ?: break
            found = true
            mil = mil || (a and 0x80) != 0
            total += a and 0x7F
            i = idx + 6
        }
        return if (found) MonitorStatus(milOn = mil, storedDtcCount = total) else null
    }
}
