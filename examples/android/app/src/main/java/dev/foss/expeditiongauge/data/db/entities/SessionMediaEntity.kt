package dev.foss.expeditiongauge.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class SessionMediaKind {
    PHOTO,
    VIDEO,
}

@Entity(
    tableName = "session_media",
    foreignKeys = [
        ForeignKey(
            entity = RecordingSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class SessionMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val timestampMs: Long,
    val fileName: String,
    val mimeType: String,
    val mediaKind: SessionMediaKind = SessionMediaKind.PHOTO,
    val bytesOnDisk: Long = 0L,
)
