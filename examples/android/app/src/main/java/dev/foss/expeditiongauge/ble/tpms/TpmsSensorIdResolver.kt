package dev.foss.expeditiongauge.ble.tpms

/**
 * Resolve short QR binding IDs to BLE MACs for BR/DJTPMS/Moman-style sensors.
 *
 * Community (eucplanet, omadon): printed ID is the last 3 bytes of the advertising MAC.
 * We **only** accept live advertisement suffix matches — never invent an OUI
 * (`AC:15:85` vs `3B:60:00` guesses caused wrong bindings).
 */
object TpmsSensorIdResolver {
    fun matchLiveMacs(macAddresses: Collection<String>, hexSuffix: String): List<String> {
        val suffix = hexSuffix.filter { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }.uppercase()
        if (suffix.length !in 4..8 || suffix.length % 2 != 0) return emptyList()
        return macAddresses
            .map { it.trim().uppercase() }
            .filter { mac ->
                val compact = mac.filter { it.isLetterOrDigit() }
                compact.endsWith(suffix)
            }
            .distinct()
            .sorted()
    }

    fun candidates(macAddresses: Collection<String>, hexSuffix: String): List<TpmsIdCandidate> =
        matchLiveMacs(macAddresses, hexSuffix).map {
            TpmsIdCandidate(it, TpmsIdCandidate.Source.LiveAdvertisement)
        }
}

data class TpmsIdCandidate(
    val macAddress: String,
    val source: Source,
) {
    enum class Source {
        LiveAdvertisement,
        @Deprecated("OUI guessing removed — caused wrong Moman bindings")
        BrOuiGuess,
    }
}
