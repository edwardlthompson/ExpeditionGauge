package dev.foss.expeditiongauge.obd

import java.io.BufferedReader
import java.io.OutputStreamWriter

/** Blocking ELM AT / PID IO with prompt wait (does not use ready()-only spins). */
internal object Elm327Io {
    private const val MAX_CHARS = 4096
    private const val POLL_SLEEP_MS = 10L

    fun sendCommand(
        reader: BufferedReader,
        writer: OutputStreamWriter,
        cmd: String,
        timeoutMs: Long,
    ): String? {
        writer.write("$cmd\r")
        writer.flush()
        return readUntilPrompt(reader, timeoutMs)
    }

    /**
     * Wait up to [timeoutMs] for ELM `>` prompt. Sleeps briefly when the stream
     * has no bytes yet (Bluetooth often arrives after the write returns).
     */
    fun readUntilPrompt(reader: BufferedReader, timeoutMs: Long = 3_000L): String? {
        val deadline = System.nanoTime() + timeoutMs * 1_000_000L
        val sb = StringBuilder()
        while (System.nanoTime() < deadline && sb.length < MAX_CHARS) {
            if (!reader.ready()) {
                try {
                    Thread.sleep(POLL_SLEEP_MS)
                } catch (_: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
                continue
            }
            val c = reader.read()
            if (c < 0) break
            sb.append(c.toChar())
            if (c.toChar() == '>') return sb.toString()
        }
        return sb.toString().ifBlank { null }
    }
}
