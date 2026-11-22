package dev.foss.expeditiongauge.obd.dtc

import android.content.Context
import org.json.JSONObject
import java.io.InputStream

/**
 * Slim OBDex CC0 lookup (code → English title). Same catalog OBDForge uses;
 * no GPL sources. Provenance: `assets/dtc/README.md`.
 *
 * Repo stores JSON shards `assets/dtc/obdex_en_*.json` (each under the 500 KB
 * hygiene budget) so F-Droid need not scanignore a gzip blob.
 */
class DtcCatalog(private val titles: Map<String, String>) {
    fun describe(code: String): String =
        titles[code.trim().uppercase()] ?: UNKNOWN

    fun size(): Int = titles.size

    companion object {
        const val UNKNOWN = "Unknown code"
        const val ASSET_DIR = "dtc"
        const val ASSET_PREFIX = "obdex_en"
        const val ASSET_SUFFIX = ".json"

        fun load(context: Context): DtcCatalog {
            val listed = context.assets.list(ASSET_DIR)
                ?.filter { it.startsWith(ASSET_PREFIX) && it.endsWith(ASSET_SUFFIX) }
                ?.sorted()
                .orEmpty()
            val names = listed.ifEmpty { (1..8).map { "${ASSET_PREFIX}_$it$ASSET_SUFFIX" } }
            val map = HashMap<String, String>()
            var opened = 0
            for (name in names) {
                val stream = runCatching { context.assets.open("$ASSET_DIR/$name") }.getOrNull()
                    ?: continue
                stream.use { loadInto(it, map) }
                opened++
            }
            require(opened > 0) { "missing OBDex JSON shards under $ASSET_DIR/" }
            return DtcCatalog(map)
        }

        fun load(stream: InputStream): DtcCatalog {
            val map = HashMap<String, String>()
            loadInto(stream, map)
            return DtcCatalog(map)
        }

        fun of(titles: Map<String, String>): DtcCatalog = DtcCatalog(titles)

        private fun loadInto(stream: InputStream, map: MutableMap<String, String>) {
            val obj = JSONObject(stream.bufferedReader(Charsets.UTF_8).readText())
            val keys = obj.keys()
            while (keys.hasNext()) {
                val code = keys.next()
                map[code.uppercase()] = obj.getString(code)
            }
        }
    }
}
