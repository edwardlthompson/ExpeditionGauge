package dev.foss.expeditiongauge.liveencrypt

/** Optional reversible XOR seal for live payloads (no org.json). */
object LiveEncrypt {
    @Volatile
    var activeKey: String? = null

    fun apply(payload: String, key: String? = activeKey): String {
        if (key.isNullOrBlank()) return payload
        return "enc|" + xorToHex(payload, key)
    }

    fun open(payload: String, key: String? = activeKey): String? {
        if (!payload.startsWith("enc|")) return payload
        if (key.isNullOrBlank()) return null
        return xorFromHex(payload.removePrefix("enc|"), key)
    }

    private fun xorToHex(text: String, key: String): String =
        text.toByteArray(Charsets.UTF_8).mapIndexed { index, byte ->
            val mixed = byte.toInt() xor key[index % key.length].code
            "%02x".format(mixed and 0xff)
        }.joinToString("")

    private fun xorFromHex(hex: String, key: String): String? {
        if (hex.length % 2 != 0) return null
        val bytes = hex.chunked(2).mapIndexed { index, pair ->
            val raw = pair.toIntOrNull(16) ?: return null
            (raw xor key[index % key.length].code).toByte()
        }.toByteArray()
        return bytes.toString(Charsets.UTF_8)
    }
}
