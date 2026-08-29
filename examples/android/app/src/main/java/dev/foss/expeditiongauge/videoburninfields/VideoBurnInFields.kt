package dev.foss.expeditiongauge.videoburninfields

/** Selectable burn-in overlay field ids. */
object VideoBurnInFields {
    val ALL = listOf("speed", "beta", "latG", "lonG", "pitch", "roll")

    fun parse(raw: String?): Set<String> =
        raw?.split(',')?.map { it.trim() }?.filter { it in ALL }?.toSet().orEmpty()

    fun encode(cols: Set<String>): String = ALL.filter { it in cols }.joinToString(",")

    fun pick(enabled: Set<String>, lines: Map<String, String>): List<String> {
        val keep = enabled.filter { it in ALL }.ifEmpty { ALL }
        return keep.mapNotNull { lines[it] }
    }
}
