package dev.foss.expeditiongauge.recording

import dev.foss.expeditiongauge.data.db.dao.RecordingSessionDao

class SessionMetadataRepository(
    private val sessionDao: RecordingSessionDao,
) {
    suspend fun get(sessionId: Long): SessionMetadata? =
        sessionDao.getById(sessionId)?.let(SessionMetadata::fromEntity)

    suspend fun save(sessionId: Long, metadata: SessionMetadata) {
        val entity = sessionDao.getById(sessionId) ?: return
        sessionDao.update(metadata.applyTo(entity))
    }
}
