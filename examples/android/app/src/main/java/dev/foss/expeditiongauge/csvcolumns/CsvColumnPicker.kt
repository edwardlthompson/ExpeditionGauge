package dev.foss.expeditiongauge.csvcolumns

/** Filters CSV header/rows to the enabled column names. */
object CsvColumnPicker {
    val ALL = listOf(
        "timestampMs", "lat", "lon", "alt", "speedMps", "headingDeg",
        "pitchDeg", "rollDeg", "latG", "lonAccel", "driftAngleDeg",
        "rpm", "throttle", "load",
    )

    fun parse(raw: String?): Set<String> =
        raw?.split(',')?.map { it.trim() }?.filter { it in ALL }?.toSet().orEmpty()

    fun encode(cols: Set<String>): String = ALL.filter { it in cols }.joinToString(",")

    fun toggle(current: Set<String>, column: String): Set<String> {
        if (column !in ALL) return current
        return if (column in current) current - column else current + column
    }

    fun filterCsv(csv: String, enabled: Set<String>): String {
        val keep = enabled.filter { it in ALL }.ifEmpty { ALL }
        val lines = csv.lines()
        if (lines.isEmpty()) return csv
        val headers = lines.first().split(',')
        val idx = headers.mapIndexedNotNull { i, name -> i.takeIf { name in keep } }
        if (idx.isEmpty()) return csv
        return lines.joinToString("\n") { line ->
            val cells = splitCsvLine(line)
            idx.joinToString(",") { i -> cells.getOrElse(i) { "" } }
        }
    }

    private fun splitCsvLine(line: String): List<String> {
        val out = mutableListOf<String>()
        val buf = StringBuilder()
        var quoted = false
        line.forEach { ch ->
            when {
                ch == '"' -> quoted = !quoted
                ch == ',' && !quoted -> {
                    out += buf.toString()
                    buf.clear()
                }
                else -> buf.append(ch)
            }
        }
        out += buf.toString()
        return out
    }
}
