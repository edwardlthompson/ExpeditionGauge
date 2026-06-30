package dev.foss.expeditiongauge.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.foss.expeditiongauge.data.db.entities.SessionEventEntity
import dev.foss.expeditiongauge.data.db.entities.SettingsProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionEventDao {
    @Insert
    suspend fun insert(entity: SessionEventEntity): Long
}

@Dao
interface SettingsProfileDao {
    @Insert
    suspend fun insert(entity: SettingsProfileEntity): Long

    @Update
    suspend fun update(entity: SettingsProfileEntity)

    @Query("SELECT * FROM settings_profiles ORDER BY id ASC")
    fun observeAll(): Flow<List<SettingsProfileEntity>>

    @Query("SELECT * FROM settings_profiles WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SettingsProfileEntity?

    @Query("SELECT COUNT(*) FROM settings_profiles")
    suspend fun count(): Int
}
