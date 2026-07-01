package dev.foss.expeditiongauge.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.foss.expeditiongauge.data.db.entities.SessionMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionMediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SessionMediaEntity): Long

    @Query("SELECT * FROM session_media WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    suspend fun getBySession(sessionId: Long): List<SessionMediaEntity>

    @Query("SELECT * FROM session_media WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    fun observeBySession(sessionId: Long): Flow<List<SessionMediaEntity>>

    @Query("SELECT * FROM session_media WHERE id = :id")
    suspend fun getById(id: Long): SessionMediaEntity?

    @Query("DELETE FROM session_media WHERE sessionId = :sessionId")
    suspend fun deleteForSession(sessionId: Long)

    @Query("SELECT COALESCE(SUM(bytesOnDisk), 0) FROM session_media")
    suspend fun totalBytes(): Long
}
