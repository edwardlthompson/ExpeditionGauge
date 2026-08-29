package dev.foss.expeditiongauge.nmealogexport

/** Ring buffer of raw NMEA sentences for share/export. */
object NmeaLogExport {
    const val MAX_LINES = 2_000

    val buffer = ArrayDeque<String>()

    fun retain(line: String, maxLines: Int = MAX_LINES) {
        val trimmed = line.trim()
        if (!trimmed.startsWith("$")) return
        synchronized(buffer) {
            buffer.addLast(trimmed)
            while (buffer.size > maxLines) buffer.removeFirst()
        }
    }

    fun snapshot(): List<String> = synchronized(buffer) { buffer.toList() }

    fun toFileText(lines: Collection<String> = snapshot()): String =
        lines.joinToString("\n")

    fun clear() {
        synchronized(buffer) { buffer.clear() }
    }
}
