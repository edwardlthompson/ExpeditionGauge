package dev.foss.expeditiongauge.hudtile

enum class HudTileId { ATTITUDE, TELEMETRY, TPMS }

object HudTileLayout {
    val DEFAULT: List<HudTileId> = listOf(HudTileId.ATTITUDE, HudTileId.TELEMETRY, HudTileId.TPMS)

    fun parse(raw: String?): List<HudTileId> {
        if (raw.isNullOrBlank()) return DEFAULT
        val parsed = raw.split(',').mapNotNull { token ->
            runCatching { HudTileId.valueOf(token.trim()) }.getOrNull()
        }
        if (parsed.isEmpty()) return DEFAULT
        val missing = DEFAULT.filter { it !in parsed }
        return parsed.distinct() + missing
    }

    fun encode(ids: List<HudTileId>): String = ids.joinToString(",") { it.name }

    fun cycle(ids: List<HudTileId>): List<HudTileId> {
        val full = parse(encode(ids))
        if (full.isEmpty()) return DEFAULT
        return full.drop(1) + full.first()
    }

    fun <T> arrange(ids: List<HudTileId>, available: Map<HudTileId, T>): List<T> {
        val ordered = ids.mapNotNull { available[it] }
        val extra = available.filterKeys { it !in ids }.values
        return ordered + extra
    }

    fun summary(ids: List<HudTileId>): String = ids.joinToString(" · ") { it.name }
}
