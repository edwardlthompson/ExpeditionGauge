package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import org.json.JSONArray
import org.json.JSONObject

data class SessionMetadata(
    val notes: String? = null,
    val driverName: String? = null,
    val conditions: String? = null,
    val vehicleConfig: Map<String, String> = emptyMap(),
    val tags: List<String> = emptyList(),
    val photoUri: String? = null,
) {
    fun toEntityPatch(): (RecordingSessionEntity) -> RecordingSessionEntity = { entity ->
        entity.copy(
            notes = notes,
            driverName = driverName,
            conditions = conditions,
            vehicleConfigJson = vehicleConfigToJson(vehicleConfig),
            tagsJson = tagsToJson(tags),
            photoUri = photoUri,
        )
    }

    fun applyTo(entity: RecordingSessionEntity): RecordingSessionEntity =
        entity.copy(
            notes = notes,
            driverName = driverName,
            conditions = conditions,
            vehicleConfigJson = vehicleConfigToJson(vehicleConfig),
            tagsJson = tagsToJson(tags),
            photoUri = photoUri,
        )

    fun toExportJson(): JSONObject = JSONObject().apply {
        putOpt("notes", notes)
        putOpt("driverName", driverName)
        putOpt("conditions", conditions)
        put("tags", JSONArray(tags))
        putOpt("photoUri", photoUri)
        put("vehicleConfig", JSONObject(vehicleConfig))
    }

    companion object {
        fun fromEntity(entity: RecordingSessionEntity): SessionMetadata =
            SessionMetadata(
                notes = entity.notes,
                driverName = entity.driverName,
                conditions = entity.conditions,
                vehicleConfig = parseVehicleConfig(entity.vehicleConfigJson),
                tags = parseTags(entity.tagsJson),
                photoUri = entity.photoUri,
            )

        fun vehicleConfigToJson(config: Map<String, String>): String? {
            if (config.isEmpty()) return null
            return buildString {
                append('{')
                config.entries.forEachIndexed { index, (key, value) ->
                    if (index > 0) append(',')
                    append('"').append(key).append("\":\"").append(value).append('"')
                }
                append('}')
            }
        }

        fun tagsToJson(tags: List<String>): String? {
            if (tags.isEmpty()) return null
            return tags.joinToString(",", "[", "]") { "\"$it\"" }
        }

        fun parseVehicleConfig(json: String?): Map<String, String> {
            if (json.isNullOrBlank()) return emptyMap()
            val trimmed = json.trim().removePrefix("{").removeSuffix("}")
            if (trimmed.isBlank()) return emptyMap()
            return trimmed.split(',').mapNotNull { pair ->
                val parts = pair.split(':', limit = 2)
                if (parts.size != 2) return@mapNotNull null
                val key = parts[0].trim().trim('"')
                val value = parts[1].trim().trim('"')
                key to value
            }.toMap()
        }

        fun parseTags(json: String?): List<String> {
            if (json.isNullOrBlank()) return emptyList()
            val inner = json.trim().removePrefix("[").removeSuffix("]")
            if (inner.isBlank()) return emptyList()
            return inner.split(',').map { it.trim().trim('"') }
        }
    }
}
