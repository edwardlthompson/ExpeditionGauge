package dev.foss.expeditiongauge.settingsqrtransfer

import dev.foss.expeditiongauge.settingsjsonbackup.SettingsJsonBackup

/** Local QR framing for a settings backup blob. No org.json. */
object SettingsQrTransfer {
    const val PREFIX = "egset|v1|"

    fun frame(blob: String): String = PREFIX + blob

    fun parse(payload: String): String? {
        if (!payload.startsWith(PREFIX)) return null
        val blob = payload.removePrefix(PREFIX)
        val decoded = SettingsJsonBackup.decode(blob)
        if (decoded.isEmpty() && blob.isNotBlank()) return null
        return blob
    }

    fun encodePairs(pairs: Map<String, String>): String =
        frame(SettingsJsonBackup.encode(pairs))
}
