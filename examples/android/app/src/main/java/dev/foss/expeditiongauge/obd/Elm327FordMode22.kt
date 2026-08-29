package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.fordmode22.FordMode22Catalog
import dev.foss.expeditiongauge.fordmode22.FordMode22Kind
import dev.foss.expeditiongauge.fordmode22.FordMode22Pid
import dev.foss.expeditiongauge.fordmode22.FordMode22TempLine
import dev.foss.expeditiongauge.fordmode22.FordMode22Temps
import java.io.BufferedReader
import java.io.OutputStreamWriter

internal object Elm327FordMode22 {
    fun requestTemps(reader: BufferedReader, writer: OutputStreamWriter): FordMode22Temps? {
        Elm327Io.sendCommand(reader, writer, "ATSH7E0", timeoutMs = 1_500L)
        return try {
            val snap = FordMode22Temps(
                transC = firstValue(reader, writer, FordMode22Kind.TRANS_TEMP),
                egtC = firstValue(reader, writer, FordMode22Kind.EGT),
            )
            snap.takeIf { FordMode22TempLine.line(it) != null }
        } finally {
            Elm327Io.sendCommand(reader, writer, "ATSH7DF", timeoutMs = 1_500L)
        }
    }

    private fun firstValue(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        kind: FordMode22Kind,
    ): Float? {
        for (pid in FordMode22Catalog.byKind(kind)) {
            read(reader, writer, pid)?.let { return it }
        }
        return null
    }

    private fun read(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        pid: FordMode22Pid,
    ): Float? {
        val raw = Elm327Protocol.queryPid(reader, writer, pid.command, timeoutMs = 2_000L)
        return FordMode22Catalog.parse(raw, pid)
    }
}
