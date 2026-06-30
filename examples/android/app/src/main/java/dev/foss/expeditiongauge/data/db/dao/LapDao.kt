package dev.foss.expeditiongauge.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.foss.expeditiongauge.data.db.entities.LapEntity

@Dao
interface LapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laps: List<LapEntity>)

    @Query("SELECT * FROM laps WHERE sessionId = :sessionId ORDER BY lapNumber ASC")
    suspend fun getBySession(sessionId: Long): List<LapEntity>

    @Query("SELECT MIN(durationMs) FROM laps WHERE sessionId = :sessionId AND isValid = 1")
    suspend fun bestLapMs(sessionId: Long): Long?
}
