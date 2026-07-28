package dev.foss.expeditiongauge.ble.tpms

/**
 * Normalize TPMS module QR / pasted text.
 *
 * Moman C4 / DJTPMS / SYTPMS "BR" sensors (UUID 0x27A5) usually print a short
 * binding ID (4–8 hex) matching the BLE MAC suffix — not a full AA:BB:… MAC.
 * See omadon/TPMS_BLE_BR, andi38/TPMS, Zen3515/djtpms.
 */
object TpmsQrPayloadParser {
    private val colonMac = Regex(
        """(?i)(?:^|[^0-9A-F])([0-9A-F]{2}(?::[0-9A-F]{2}){5})(?:$|[^0-9A-F])""",
    )
    private val dashMac = Regex(
        """(?i)(?:^|[^0-9A-F])([0-9A-F]{2}(?:-[0-9A-F]{2}){5})(?:$|[^0-9A-F])""",
    )
    private val macQuery = Regex("""(?i)(?:[?&#]|^)mac=([0-9A-F:.\-]{12,17})""")
    private val compactHex = Regex("""(?i)^[0-9A-F]{12}$""")
    private val shortId = Regex("""(?i)^[0-9A-F]{4}$|^[0-9A-F]{6}$|^[0-9A-F]{8}$""")

    fun parse(raw: String?): TpmsQrParseResult {
        if (raw.isNullOrBlank()) return TpmsQrParseResult.Invalid(TpmsQrParseResult.Reason.Empty)
        val text = raw.trim()
        macQuery.find(text)?.groupValues?.getOrNull(1)?.let { candidate ->
            return normalizeCandidate(candidate)
        }
        colonMac.find(text)?.groupValues?.getOrNull(1)?.let { return normalizeCandidate(it) }
        dashMac.find(text)?.groupValues?.getOrNull(1)?.let { return normalizeCandidate(it) }
        val stripped = text.filter { it.isLetterOrDigit() }.uppercase()
        if (compactHex.matches(stripped)) return normalizeCandidate(stripped)
        if (shortId.matches(stripped)) {
            return TpmsQrParseResult.SensorId(stripped)
        }
        if (stripped.length in 1..11 && stripped.all { it.isDigit() || it in 'A'..'F' }) {
            return TpmsQrParseResult.Invalid(TpmsQrParseResult.Reason.BadLength)
        }
        return TpmsQrParseResult.Invalid(TpmsQrParseResult.Reason.NoMac)
    }

    private fun normalizeCandidate(candidate: String): TpmsQrParseResult {
        val hex = candidate.filter { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }.uppercase()
        if (shortId.matches(hex)) return TpmsQrParseResult.SensorId(hex)
        if (hex.length != 12) {
            return if (hex.any { !it.isDigit() && it !in 'A'..'F' }) {
                TpmsQrParseResult.Invalid(TpmsQrParseResult.Reason.BadCharset)
            } else {
                TpmsQrParseResult.Invalid(TpmsQrParseResult.Reason.BadLength)
            }
        }
        val mac = hex.chunked(2).joinToString(":")
        return TpmsQrParseResult.Ok(mac)
    }
}

sealed class TpmsQrParseResult {
    data class Ok(val macAddress: String) : TpmsQrParseResult()
    /** Short binding ID from Moman/DJTPMS/BR sensor QR (MAC suffix). */
    data class SensorId(val hexSuffix: String) : TpmsQrParseResult()
    data class Invalid(val reason: Reason) : TpmsQrParseResult()

    enum class Reason {
        Empty,
        NoMac,
        BadLength,
        BadCharset,
    }
}
