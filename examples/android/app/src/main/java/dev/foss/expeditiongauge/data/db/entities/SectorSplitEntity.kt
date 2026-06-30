package dev.foss.expeditiongauge.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sector_splits",
    foreignKeys = [
        ForeignKey(
            entity = LapEntity::class,
            parentColumns = ["id"],
            childColumns = ["lapId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("lapId")],
)
data class SectorSplitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lapId: Long,
    val sectorIndex: Int,
    val splitMs: Long,
    val sampleId: Long,
)
