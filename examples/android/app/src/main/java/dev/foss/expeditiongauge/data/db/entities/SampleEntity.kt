package dev.foss.expeditiongauge.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "samples",
    foreignKeys = [
        ForeignKey(
            entity = RecordingSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId"), Index("timestampMs")],
)
data class SampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val timestampMs: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val altitudeM: Double? = null,
    val speedMps: Float = 0f,
    val headingDeg: Float = 0f,
    val pitchDeg: Float = 0f,
    val rollDeg: Float = 0f,
    val lonAccel: Float = 0f,
    val latG: Float = 0f,
    val yawRate: Float = 0f,
    val driftAngleDeg: Float? = null,
    val bodyYawDeg: Float? = null,
    val velocityHeadingDeg: Float? = null,
    val throttle: Float? = null,
    val rpm: Float? = null,
    val load: Float? = null,
    val slipRatio: Float? = null,
    val fuelRateLph: Float? = null,
    val extrasJson: String? = null,
)
