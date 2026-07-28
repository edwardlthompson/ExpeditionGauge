package dev.foss.expeditiongauge.obd.dtc

import android.content.Context
import org.json.JSONObject
import java.io.InputStream

/**
 * Slim OBDex CC0 lookup (code → English title). Same catalog OBDForge uses;
 * no GPL sources. Asset: `dtc/obdex_en.json`.
 */
class DtcCatalog(private val titles: Map<String, String>) {
    fun describe(code: String): String =
        titles[code.trim().uppercase()] ?: UNKNOWN

    fun size(): Int = titles.size

    companion object {
        const val UNKNOWN = "Unknown code"
        const val ASSET_PATH = "dtc/obdex_en.json"

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
