package dev.foss.expeditiongauge.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.foss.expeditiongauge.data.db.entities.AlertEventEntity

@Dao
interface AlertEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: AlertEventEntity): Long

    @Query("SELECT * FROM alert_events WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    suspend fun getBySession(sessionId: Long): List<AlertEventEntity>
}
