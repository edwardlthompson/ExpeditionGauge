package dev.foss.expeditiongauge.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.foss.expeditiongauge.data.db.entities.TrackConfigEntity

@Dao
interface TrackConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: TrackConfigEntity): Long

    @Query("SELECT * FROM track_configs WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getForSession(sessionId: Long): TrackConfigEntity?
}
