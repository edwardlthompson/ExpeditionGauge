package dev.foss.expeditiongauge.obd

/** SAE J1979 Mode 01 PID-support bitmaps (`0100` → PIDs 01–20, `0140` → 41–60). */
object ObdPidSupport {
    fun parseBitmap(hex: String, replyHeader: String, firstPid: Int): Set<Int> {
        val idx = hex.indexOf(replyHeader)
        if (idx < 0 || idx + replyHeader.length + 8 > hex.length) return emptySet()
        val data = hex.substring(idx + replyHeader.length, idx + replyHeader.length + 8)
        val bytes = IntArray(4) { i ->
            data.substring(i * 2, i * 2 + 2).toIntOrNull(16) ?: return emptySet()
        }
        val supported = linkedSetOf<Int>()
        for (i in 0 until 32) {
            val bit = 7 - (i % 8)
            if (bytes[i / 8] and (1 shl bit) != 0) supported += firstPid + i
        }
        return supported
    }
}
