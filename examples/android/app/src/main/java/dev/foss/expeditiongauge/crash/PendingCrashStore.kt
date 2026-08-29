package dev.foss.expeditiongauge.crash

import android.content.Context
import dev.foss.expeditiongauge.privacyreport.SanitizeReport
import java.io.File

data class PendingCrash(val message: String, val stack: String)

class PendingCrashStore(private val context: Context) {
    private fun file(): File = File(context.filesDir, FILE)

    fun write(record: PendingCrash): Boolean =
        try {
            file().writeText(encode(record))
            true
        } catch (_: Exception) {
            false
        }

    fun read(): PendingCrash? =
        try {
            val raw = file().takeIf { it.isFile }?.readText() ?: return null
            decode(raw)
        } catch (_: Exception) {
            null
        }

    fun clear() {
        runCatching { file().delete() }
    }

    companion object {
        const val FILE = "eg_pending_crash.txt"

        fun encode(record: PendingCrash): String {
            val msg = SanitizeReport.text(record.message)
            val stack = SanitizeReport.text(record.stack, stack = true)
            return "$msg\n---\n$stack"
        }

        fun decode(raw: String): PendingCrash {
            val parts = raw.split("\n---\n", limit = 2)
            return PendingCrash(parts[0], parts.getOrElse(1) { "" })
        }
    }
}
