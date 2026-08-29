package dev.foss.expeditiongauge.alerthistory

import org.json.JSONArray
import org.json.JSONObject

data class AlertHistoryEntry(
    val type: String,
    val value: Float,
    val threshold: Float,
    val timestampMs: Long,
)

object AlertHistory {
    const val MAX = 50

    fun append(existing: List<AlertHistoryEntry>, next: AlertHistoryEntry): List<AlertHistoryEntry> =
        (listOf(next) + existing).take(MAX)

    fun encode(entries: List<AlertHistoryEntry>): String {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(
                JSONObject()
                    .put("type", entry.type)
                    .put("value", entry.value.toDouble())
                    .put("threshold", entry.threshold.toDouble())
                    .put("ts", entry.timestampMs),
            )
        }
        return array.toString()
    }

    fun decode(raw: String?): List<AlertHistoryEntry> {
        if (raw.isNullOrBlank()) return emptyList()
        val array = runCatching { JSONArray(raw) }.getOrNull() ?: return emptyList()
        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                add(
                    AlertHistoryEntry(
                        type = obj.optString("type"),
                        value = obj.optDouble("value").toFloat(),
                        threshold = obj.optDouble("threshold").toFloat(),
                        timestampMs = obj.optLong("ts"),
                    ),
                )
            }
        }
    }
}
