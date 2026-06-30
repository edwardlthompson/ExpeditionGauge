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

    @Query("SELECT * FROM recording_sessions WHERE id = :id")
    suspend fun getById(id: Long): RecordingSessionEntity?

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
