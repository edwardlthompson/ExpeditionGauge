package dev.foss.expeditiongauge.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.foss.expeditiongauge.recording.ActivityType
import dev.foss.expeditiongauge.recording.RecordingMode

@Entity(tableName = "recording_sessions")
data class RecordingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startTimeMs: Long,
    val endTimeMs: Long? = null,
    val recordingMode: RecordingMode = RecordingMode.NORMAL,
    val activityType: ActivityType = ActivityType.DRIVE,
    val deviceConfigJson: String? = null,
    val notes: String? = null,
    val driverName: String? = null,
    val conditions: String? = null,
    val vehicleConfigJson: String? = null,
    val tagsJson: String? = null,
    val photoUri: String? = null,
    val videoUri: String? = null,
    val videoOffsetMs: Long = 0L,
    val protectedFromLoop: Boolean = false,
    val autoRecordTriggerAddress: String? = null,
)
