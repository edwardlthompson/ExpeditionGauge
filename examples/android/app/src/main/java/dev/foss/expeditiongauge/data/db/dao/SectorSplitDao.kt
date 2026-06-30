package dev.foss.expeditiongauge.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.foss.expeditiongauge.data.db.entities.SectorSplitEntity

@Dao
interface SectorSplitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(splits: List<SectorSplitEntity>)

    @Query("SELECT * FROM sector_splits WHERE lapId = :lapId ORDER BY sectorIndex ASC")
    suspend fun getByLap(lapId: Long): List<SectorSplitEntity>
}
