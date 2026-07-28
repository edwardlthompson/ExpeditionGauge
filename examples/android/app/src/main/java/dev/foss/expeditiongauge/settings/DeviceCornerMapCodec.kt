package dev.foss.expeditiongauge.settings

import dev.foss.expeditiongauge.ble.ImuPlacement

/** Encode/decode MAC→placement maps for DataStore (`AA:BB=FL,CC:DD=FR`). */
object DeviceCornerMapCodec {
    fun encode(map: Map<String, ImuPlacement>): String =
        map.entries
            .filter { it.value != ImuPlacement.Unassigned }
            .joinToString(",") { "${it.key}=${it.value.name}" }

    fun decode(raw: String?): Map<String, ImuPlacement> {
        if (raw.isNullOrBlank()) return emptyMap()
        return raw.split(',')
            .mapNotNull { part ->
                val idx = part.indexOf('=')
                if (idx <= 0) return@mapNotNull null
                val mac = part.substring(0, idx).trim()
                val placement = ImuPlacement.fromLabel(part.substring(idx + 1).trim())
                if (mac.isEmpty() || placement == ImuPlacement.Unassigned) null else mac to placement
            }
            .toMap()
    }
}
