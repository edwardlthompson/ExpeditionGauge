package dev.foss.expeditiongauge.playback

import dev.foss.expeditiongauge.data.db.entities.SampleEntity

data class SampleGpsInfo(
    val gpsSource: String = "phone",
    val hdop: Float? = null,
    val numSatellites: Int? = null,
    val fixQuality: Int = 0,
)

object SampleGpsMetadata {
    fun fromSample(sample: SampleEntity): SampleGpsInfo {
        val json = sample.extrasJson ?: return SampleGpsInfo()
        return SampleGpsInfo(
            gpsSource = stringField(json, "gpsSource") ?: "phone",
            hdop = floatField(json, "hdop"),
            numSatellites = intField(json, "numSatellites"),
            fixQuality = intField(json, "fixQuality") ?: 0,
        )
    }

    fun prefersExternal(sample: SampleEntity): Boolean =
        fromSample(sample).gpsSource.equals("external", ignoreCase = true)

    private fun stringField(json: String, key: String): String? =
        """"$key"\s*:\s*"([^"]*)"""".toRegex().find(json)?.groupValues?.get(1)

    private fun floatField(json: String, key: String): Float? =
        """"$key"\s*:\s*([-\d.]+)""".toRegex().find(json)?.groupValues?.get(1)?.toFloatOrNull()

    private fun intField(json: String, key: String): Int? =
        """"$key"\s*:\s*(\d+)""".toRegex().find(json)?.groupValues?.get(1)?.toIntOrNull()
}
