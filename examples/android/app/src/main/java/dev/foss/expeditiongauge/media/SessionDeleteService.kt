package dev.foss.expeditiongauge.media

import dev.foss.expeditiongauge.data.db.dao.RecordingSessionDao

class SessionDeleteService(
    private val sessionDao: RecordingSessionDao,
    private val mediaRepository: SessionMediaRepository,
) {
    suspend fun deleteSession(sessionId: Long) {
        mediaRepository.deleteSessionFiles(sessionId)
        sessionDao.deleteById(sessionId)
    }
}
