package dev.foss.expeditiongauge.playback

data class ImuDeviceCorner(
    val deviceId: String,
    val placement: String,
    val latG: Float? = null,
    val yawDeg: Float? = null,
)

object SampleImuExtras {
    fun corners(extrasJson: String?): List<ImuDeviceCorner> {
        if (extrasJson.isNullOrBlank() || !extrasJson.contains("imuDevices")) return emptyList()
        val devices = mutableListOf<ImuDeviceCorner>()
        val blockPattern = """\{[^{}]*"deviceId"[^{}]*\}""".toRegex()
        blockPattern.findAll(extrasJson).forEach { match ->
            val block = match.value
            val deviceId = stringField(block, "deviceId") ?: return@forEach
            devices += ImuDeviceCorner(
                deviceId = deviceId,
                placement = stringField(block, "placement") ?: "unknown",
                latG = floatField(block, "latG"),
                yawDeg = floatField(block, "filteredYawDeg") ?: floatField(block, "rawYawDeg"),
            )
        }
        return devices
    }

    fun hasMultiImu(extrasJson: String?): Boolean = corners(extrasJson).size >= 2

    private fun stringField(block: String, key: String): String? =
        """"$key"\s*:\s*"([^"]*)"""".toRegex().find(block)?.groupValues?.get(1)

    private fun floatField(block: String, key: String): Float? =
        """"$key"\s*:\s*([-\d.]+)""".toRegex().find(block)?.groupValues?.get(1)?.toFloatOrNull()
}
