package dev.foss.expeditiongauge.vehiclepidmaps

/** Per-vehicle Mode 01 PID hex lists. */
object VehiclePidMaps {
    val DEFAULT = listOf("0C", "0D", "05")

    fun pidsFor(vehicleId: String, maps: Map<String, List<String>> = emptyMap()): List<String> =
        maps[vehicleId.trim().lowercase()]?.ifEmpty { DEFAULT } ?: DEFAULT

    fun parse(blob: String): List<String> =
        blob.split(',', ' ').map { it.trim().uppercase() }.filter { it.matches(Regex("[0-9A-F]{2}")) }
}
