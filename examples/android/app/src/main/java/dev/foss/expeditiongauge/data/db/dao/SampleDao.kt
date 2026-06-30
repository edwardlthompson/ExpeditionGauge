package dev.foss.expeditiongauge.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.foss.expeditiongauge.data.db.entities.SampleEntity

@Dao
interface SampleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(samples: List<SampleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sample: SampleEntity): Long

    @Query("SELECT * FROM samples WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    suspend fun getBySession(sessionId: Long): List<SampleEntity>

    @Query("SELECT COUNT(*) FROM samples WHERE sessionId = :sessionId")
    suspend fun countBySession(sessionId: Long): Int
}
