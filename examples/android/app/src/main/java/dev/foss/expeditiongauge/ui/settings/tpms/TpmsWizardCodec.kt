package dev.foss.expeditiongauge.ui.settings.tpms

import dev.foss.expeditiongauge.ble.ImuPlacement

internal fun encodeAssigned(map: Map<ImuPlacement, String>): String =
    map.entries.joinToString(";") { "${it.key.name}=${it.value}" }

internal fun decodeAssigned(raw: String): Map<ImuPlacement, String> {
    if (raw.isBlank()) return emptyMap()
    return raw.split(';').mapNotNull { part ->
        val idx = part.indexOf('=')
        if (idx <= 0) return@mapNotNull null
        val key = runCatching { ImuPlacement.valueOf(part.substring(0, idx)) }.getOrNull()
            ?: return@mapNotNull null
        key to part.substring(idx + 1)
    }.toMap()
}

internal fun encodeSkipped(set: Set<ImuPlacement>): String =
    set.joinToString(",") { it.name }

internal fun decodeSkipped(raw: String): Set<ImuPlacement> {
    if (raw.isBlank()) return emptySet()
    return raw.split(',').mapNotNull {
        runCatching { ImuPlacement.valueOf(it) }.getOrNull()
    }.toSet()
}
