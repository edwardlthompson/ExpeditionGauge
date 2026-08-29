package dev.foss.expeditiongauge.obd

import dev.foss.expeditiongauge.fordmode22.FordMode22Catalog
import dev.foss.expeditiongauge.fordmode22.FordMode22Kind
import java.io.BufferedReader
import java.io.OutputStreamWriter

/**
 * Real-time driver demand for ETC Fords (2006 Expedition): prefer accelerator
 * pedal PIDs over Mode 01 `0111` (throttle *plate* / airflow).
 */
internal data class ObdThrottleChannel(
    val command: String,
    val header: String,
    val scale: Float,
    val pcmHeader: Boolean,
)

internal object ObdThrottleQuery {
    private const val PROBE_MS = 800L
    val plate = ObdThrottleChannel("0111", "4111", 100f / 255f, false)

    private val appGeneric = listOf(
        ObdThrottleChannel("0149", "4149", 100f / 255f, false),
        ObdThrottleChannel("014A", "414A", 100f / 255f, false),
        ObdThrottleChannel("014B", "414B", 100f / 255f, false),
    )
    private val appFord = FordMode22Catalog.byKind(FordMode22Kind.THROTTLE).map { pid ->
        ObdThrottleChannel(pid.command, pid.header, pid.scale, true)
    }
    private val known = (appGeneric + appFord + plate).associateBy { it.command }

    fun byCommand(command: String?): ObdThrottleChannel? =
        command?.let { known[it] }

    fun discover(reader: BufferedReader, writer: OutputStreamWriter): ObdThrottleChannel {
        val pids40 = Elm327Protocol.queryPid(reader, writer, "0140", timeoutMs = PROBE_MS)
            ?.let { ObdPidSupport.parseBitmap(it, "4140", 0x41) }
            ?: emptySet()
        val bitmapOk = pids40.isNotEmpty()
        for (ch in appGeneric) {
            val pid = ch.command.substring(2).toInt(16)
            if (bitmapOk && pid !in pids40) continue
            if (read(reader, writer, ch, timeoutMs = PROBE_MS) != null) return ch
        }
        return probeFord(reader, writer) ?: plate
    }

    fun read(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        channel: ObdThrottleChannel,
        timeoutMs: Long = 3_000L,
    ): Float? {
        val raw = if (channel.pcmHeader) {
            withPcmHeader(reader, writer) {
                Elm327Protocol.queryPid(reader, writer, channel.command, timeoutMs)
            }
        } else {
            Elm327Protocol.queryPid(reader, writer, channel.command, timeoutMs)
        } ?: return null
        val byte = Elm327Protocol.parsePidDataByte(raw, channel.header) ?: return null
        return (byte * channel.scale).coerceIn(0f, 100f)
    }

    private fun probeFord(
        reader: BufferedReader,
        writer: OutputStreamWriter,
    ): ObdThrottleChannel? = withPcmHeader(reader, writer) {
        appFord.firstOrNull { ch ->
            Elm327Protocol.queryPid(reader, writer, ch.command, PROBE_MS)
                ?.let { Elm327Protocol.parsePidDataByte(it, ch.header) } != null
        }
    }

    private fun <T> withPcmHeader(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        block: () -> T,
    ): T {
        Elm327Io.sendCommand(reader, writer, "ATSH7E0", timeoutMs = 1_500L)
        try {
            return block()
        } finally {
            Elm327Io.sendCommand(reader, writer, "ATSH7DF", timeoutMs = 1_500L)
        }
    }
}
