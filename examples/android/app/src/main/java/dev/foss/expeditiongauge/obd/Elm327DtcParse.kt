package dev.foss.expeditiongauge.obd

/**
 * Mode 03 / 07 DTC framing.
 * CAN (ISO 15765): `43 <count> <pairs>`. ISO/KWP/J1850: `43 <pairs>` (often 3 slots).
 */
internal object Elm327DtcParse {
    fun stripNoise(raw: String): String =
        raw.lineSequence()
            .filterNot { line ->
                val u = line.uppercase()
                u.contains("SEARCHING") || u.contains("BUS INIT") || u.trim() == "OK"
            }
            .joinToString("\n")

    fun parseServiceDtcs(raw: String, sidHex: String, canFraming: Boolean? = null): List<String> {
        val upper = raw.uppercase()
        if (upper.contains("NO DATA") || upper.contains("UNABLE") || upper.contains("ERROR")) {
            return emptyList()
        }
        val hex = Elm327Protocol.normalizeElmHex(stripNoise(raw))
        if (hex.isEmpty()) return emptyList()
        val sid = sidHex.uppercase()
        val idx = Elm327Protocol.indexOfEvenHex(hex, sid)
        if (idx < 0) return emptyList()
        val payload = hex.substring(idx + sid.length)
        val bytes = hexToBytes(payload)
        if (bytes.isEmpty()) return emptyList()
        val iso = pairsToCodes(bytes)
        val can = if (bytes.size >= 2) pairsToCodes(bytes.drop(1)) else emptyList()
        return when (canFraming) {
            true -> can
            false -> iso
            null -> chooseFraming(bytes, iso, can)
        }.distinct()
    }

    /** ELM `ATDPN`: 6–9 / A–C are CAN (count byte); 1–5 are ISO/KWP/J1850. */
    fun canFramingFromDpn(raw: String): Boolean? {
        val chars = stripNoise(raw).uppercase().filter { it.isLetterOrDigit() }
        val last = chars.lastOrNull { it in '1'..'9' || it in 'A'..'C' } ?: return null
        return last !in '1'..'5'
    }

    internal fun chooseFraming(bytes: List<Int>, iso: List<String>, can: List<String>): List<String> {
        val n = bytes[0]
        if (n in 0..16 && can.size == n) {
            // Classic ISO 9141 3-slot (6 bytes) looks like CAN count=1..3.
            if (bytes.size == 6 && iso.size == n && n <= 3) return iso
            return can
        }
        return iso
    }

    private fun hexToBytes(hex: String): List<Int> {
        val out = ArrayList<Int>(hex.length / 2)
        var i = 0
        while (i + 2 <= hex.length) {
            out.add(hex.substring(i, i + 2).toIntOrNull(16) ?: break)
            i += 2
        }
        return out
    }

    private fun pairsToCodes(bytes: List<Int>): List<String> {
        val codes = ArrayList<String>(bytes.size / 2)
        var i = 0
        while (i + 1 < bytes.size) {
            val b1 = bytes[i]
            val b2 = bytes[i + 1]
            i += 2
            if (b1 == 0 && b2 == 0) continue
            codes.add(Elm327Protocol.decodeDtcBytes(b1, b2))
        }
        return codes
    }
}
