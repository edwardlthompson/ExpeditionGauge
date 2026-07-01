package dev.foss.expeditiongauge.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dev.foss.expeditiongauge.data.db.dao.AlertEventDao
import dev.foss.expeditiongauge.data.db.dao.LapDao
import dev.foss.expeditiongauge.data.db.dao.RecordingSessionDao
import dev.foss.expeditiongauge.data.db.dao.SampleDao
import dev.foss.expeditiongauge.data.db.dao.SectorSplitDao
import dev.foss.expeditiongauge.data.db.dao.SessionEventDao
import dev.foss.expeditiongauge.data.db.dao.SessionMediaDao
import dev.foss.expeditiongauge.data.db.dao.SettingsProfileDao
import dev.foss.expeditiongauge.data.db.dao.TrackConfigDao
import dev.foss.expeditiongauge.data.db.entities.AlertEventEntity
import dev.foss.expeditiongauge.data.db.entities.LapEntity
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import dev.foss.expeditiongauge.data.db.entities.SampleEntity
import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity
import dev.foss.expeditiongauge.data.db.entities.SessionEventEntity
import dev.foss.expeditiongauge.data.db.entities.SessionMediaEntity
import dev.foss.expeditiongauge.data.db.entities.SessionMediaKind
import dev.foss.expeditiongauge.data.db.entities.SettingsProfileEntity
import dev.foss.expeditiongauge.data.db.entities.TrackConfigEntity
import dev.foss.expeditiongauge.recording.ActivityType
import dev.foss.expeditiongauge.recording.RecordingMode

@Database(
    entities = [
        RecordingSessionEntity::class,
        SampleEntity::class,
        TrackConfigEntity::class,
        LapEntity::class,
        SectorSplitEntity::class,
        AlertEventEntity::class,
        SessionEventEntity::class,
        SessionMediaEntity::class,
        SettingsProfileEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
@TypeConverters(RecordingModeConverter::class, SessionMediaKindConverter::class, ActivityTypeConverter::class)
abstract class ExpeditionGaugeDatabase : RoomDatabase() {
    abstract fun recordingSessionDao(): RecordingSessionDao
    abstract fun sampleDao(): SampleDao
    abstract fun trackConfigDao(): TrackConfigDao
    abstract fun lapDao(): LapDao
    abstract fun sectorSplitDao(): SectorSplitDao
    abstract fun alertEventDao(): AlertEventDao
    abstract fun sessionEventDao(): SessionEventDao
    abstract fun sessionMediaDao(): SessionMediaDao
    abstract fun settingsProfileDao(): SettingsProfileDao

    companion object {
        fun create(context: Context): ExpeditionGaugeDatabase =
            Room.databaseBuilder(context, ExpeditionGaugeDatabase::class.java, "expedition_gauge.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}

class RecordingModeConverter {
    @TypeConverter
    fun fromMode(mode: RecordingMode): String = mode.name

    @TypeConverter
    fun toMode(value: String): RecordingMode = RecordingMode.valueOf(value)
}

class SessionMediaKindConverter {
    @TypeConverter
    fun fromKind(kind: SessionMediaKind): String = kind.name

    @TypeConverter
    fun toKind(value: String): SessionMediaKind = SessionMediaKind.valueOf(value)
}

class ActivityTypeConverter {
    @TypeConverter
    fun fromType(type: ActivityType): String = type.name

    @TypeConverter
    fun toType(value: String): ActivityType = ActivityType.valueOf(value)
}
