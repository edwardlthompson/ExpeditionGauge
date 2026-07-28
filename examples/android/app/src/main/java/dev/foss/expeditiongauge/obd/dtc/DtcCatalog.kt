package dev.foss.expeditiongauge.obd.dtc

import android.content.Context
import org.json.JSONObject
import java.io.InputStream

/**
 * Slim OBDex CC0 lookup (code → English title). Same catalog OBDForge uses;
 * no GPL sources.
 *
 * Repo stores `assets/dtc/obdex_en.gz` (gzip JSON under the 500 KB hygiene budget).
 * aapt decompresses `.gz` and strips the extension, so runtime open path is
 * [ASSET_PATH] with plain JSON bytes.
 */
class DtcCatalog(private val titles: Map<String, String>) {
    fun describe(code: String): String =
        titles[code.trim().uppercase()] ?: UNKNOWN

    fun size(): Int = titles.size

    companion object {
        const val UNKNOWN = "Unknown code"
        /** Path after aapt merge (source file `obdex_en.gz`). */
        const val ASSET_PATH = "dtc/obdex_en"

        fun load(context: Context): DtcCatalog =
            context.assets.open(ASSET_PATH).use { load(it) }

        fun load(stream: InputStream): DtcCatalog {
            val text = stream.bufferedReader(Charsets.UTF_8).readText()
            val obj = JSONObject(text)
            val map = HashMap<String, String>(obj.length())
            val keys = obj.keys()
            while (keys.hasNext()) {
                val code = keys.next()
                map[code.uppercase()] = obj.getString(code)
            }
            return DtcCatalog(map)
        }

        fun of(titles: Map<String, String>): DtcCatalog = DtcCatalog(titles)
    }
}
