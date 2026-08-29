package dev.foss.expeditiongauge.encryptedsessionzip

/** XOR-seal a session file listing. FOSS-only; no proprietary crypto SDK. */
object EncryptedSessionZip {
    const val PREFIX = "zip|"

    fun listing(names: List<String>): String =
        names.filter { it.isNotBlank() }.joinToString("|")

    fun seal(names: List<String>, key: String): String {
        if (key.isBlank()) return PREFIX + listing(names)
        return PREFIX + xorToHex(listing(names), key)
    }

    fun open(payload: String, key: String): List<String>? {
        if (!payload.startsWith(PREFIX)) return null
        val body = payload.removePrefix(PREFIX)
        val text = if (key.isBlank()) body else xorFromHex(body, key) ?: return null
        if (text.isBlank()) return emptyList()
        return text.split('|').filter { it.isNotBlank() }
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
