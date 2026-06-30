package dev.foss.expeditiongauge.live

import org.json.JSONObject

data class LiveSampleDto(
    val timestampMs: Long,
    val speedMps: Float,
    val latG: Float,
    val betaDeg: Float?,
    val pitchDeg: Float,
    val rollDeg: Float,
    val headingDeg: Float,
    val tpmsJson: String? = null,
) {
    fun toJson(): String = JSONObject()
        .put("t", timestampMs)
        .put("speed", speedMps.toDouble())
        .put("latG", latG.toDouble())
        .put("pitch", pitchDeg.toDouble())
        .put("roll", rollDeg.toDouble())
        .put("hdg", headingDeg.toDouble())
        .apply {
            betaDeg?.let { put("beta", it.toDouble()) }
            tpmsJson?.let { put("tpms", JSONObject(it)) }
        }
        .toString()
}
