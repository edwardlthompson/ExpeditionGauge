package dev.foss.expeditiongauge.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.foss.expeditiongauge.data.db.entities.RecordingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordingSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: RecordingSessionEntity): Long

    @Update
    suspend fun update(session: RecordingSessionEntity)

    @Query("SELECT * FROM recording_sessions ORDER BY startTimeMs DESC")
    fun observeAll(): Flow<List<RecordingSessionEntity>>

    @Query(
        """
        SELECT * FROM recording_sessions
        WHERE notes LIKE '%' || :query || '%'
           OR driverName LIKE '%' || :query || '%'
           OR tagsJson LIKE '%' || :query || '%'
        ORDER BY startTimeMs DESC
        """,
    )
    fun observeSearch(query: String): Flow<List<RecordingSessionEntity>>

    @Query(
        """
        SELECT * FROM recording_sessions
        WHERE (:activityType = '' OR activityType = :activityType)
          AND (
            :query = ''
            OR notes LIKE '%' || :query || '%'
            OR driverName LIKE '%' || :query || '%'
            OR tagsJson LIKE '%' || :query || '%'
            OR name LIKE '%' || :query || '%'
          )
        ORDER BY startTimeMs DESC
        """,
    )
    fun observeFiltered(activityType: String, query: String): Flow<List<RecordingSessionEntity>>

    @Query("SELECT * FROM recording_sessions WHERE id = :id")
    suspend fun getById(id: Long): RecordingSessionEntity?

    @Query(
        """
        SELECT * FROM recording_sessions
        WHERE protectedFromLoop = 0
          AND (:excludeId IS NULL OR id != :excludeId)
        ORDER BY startTimeMs ASC
        LIMIT 1
        """,
    )
    suspend fun oldestUnprotectedSession(excludeId: Long?): RecordingSessionEntity?

    @Query("DELETE FROM recording_sessions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT * FROM recording_sessions
        WHERE notes LIKE '%' || :query || '%'
           OR driverName LIKE '%' || :query || '%'
           OR tagsJson LIKE '%' || :query || '%'
        ORDER BY startTimeMs DESC
        """,
    )
    suspend fun search(query: String): List<RecordingSessionEntity>
}
