package dev.foss.expeditiongauge.settings

import dev.foss.expeditiongauge.data.db.entities.SettingsProfileEntity
import dev.foss.expeditiongauge.presets.DashboardPreset
import dev.foss.expeditiongauge.presets.DashboardPresetId
import dev.foss.expeditiongauge.recording.RecordingMode
import org.json.JSONObject

data class SettingsProfile(
    val id: Long = 0L,
    val name: String,
    val presetId: DashboardPresetId = DashboardPresetId.Default,
    val recordingMode: RecordingMode = RecordingMode.NORMAL,
    val unitsMetric: Boolean = true,
    val logRateHz: Int = 10,
    val playbackMapWeight: Float = 0.6f,
    val playbackGraphsExpanded: Boolean = true,
) {
    val dashboardPreset: DashboardPreset
        get() = DashboardPreset.fromId(presetId).copy(recordingMode = recordingMode)

    fun toEntity(): SettingsProfileEntity = SettingsProfileEntity(
        id = id,
        name = name,
        profileJson = toJson(),
    )

    fun toJson(): String = JSONObject()
        .put("presetId", presetId.name)
        .put("recordingMode", recordingMode.name)
        .put("unitsMetric", unitsMetric)
        .put("logRateHz", logRateHz)
        .put("playbackMapWeight", playbackMapWeight.toDouble())
        .put("playbackGraphsExpanded", playbackGraphsExpanded)
        .toString()

    companion object {
        fun fromEntity(entity: SettingsProfileEntity): SettingsProfile {
            val json = runCatching { JSONObject(entity.profileJson) }.getOrNull()
            return SettingsProfile(
                id = entity.id,
                name = entity.name,
                presetId = json?.optString("presetId")?.let {
                    runCatching { DashboardPresetId.valueOf(it) }.getOrDefault(DashboardPresetId.Default)
                } ?: DashboardPresetId.Default,
                recordingMode = json?.optString("recordingMode")?.let {
                    runCatching { RecordingMode.valueOf(it) }.getOrDefault(RecordingMode.NORMAL)
                } ?: RecordingMode.NORMAL,
                unitsMetric = json?.optBoolean("unitsMetric", true) ?: true,
                logRateHz = json?.optInt("logRateHz", 10) ?: 10,
                playbackMapWeight = json?.optDouble("playbackMapWeight", 0.6)?.toFloat() ?: 0.6f,
                playbackGraphsExpanded = json?.optBoolean("playbackGraphsExpanded", true) ?: true,
            )
        }

        fun defaultProfile(): SettingsProfile = SettingsProfile(
            id = 1L,
            name = "Default",
            presetId = DashboardPresetId.Default,
        )
    }
}
