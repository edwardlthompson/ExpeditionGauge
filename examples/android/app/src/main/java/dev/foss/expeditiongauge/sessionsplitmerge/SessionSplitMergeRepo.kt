package dev.foss.expeditiongauge.sessionsplitmerge

import dev.foss.expeditiongauge.data.db.dao.RecordingSessionDao
import dev.foss.expeditiongauge.data.db.dao.SampleDao

class SessionSplitMergeRepo(
    private val sessionDao: RecordingSessionDao,
    private val sampleDao: SampleDao,
    private val deleteSession: suspend (Long) -> Unit,
) {
    suspend fun splitAt(sessionId: Long, atMs: Long): Pair<Long, Long>? {
        val session = sessionDao.getById(sessionId) ?: return null
        val samples = sampleDao.getBySession(sessionId)
        val (left, right) = SessionSplitMerge.splitAt(samples, atMs)
        if (left.isEmpty() || right.isEmpty()) return null
        val leftId = sessionDao.insert(
            session.copy(
                id = 0L,
                name = SessionSplitMerge.splitName(session.name, 1),
                endTimeMs = atMs,
            ),
        )
        val rightId = sessionDao.insert(
            session.copy(
                id = 0L,
                name = SessionSplitMerge.splitName(session.name, 2),
                startTimeMs = atMs,
            ),
        )
        sampleDao.insertAll(SessionSplitMerge.remap(left, leftId))
        sampleDao.insertAll(SessionSplitMerge.remap(right, rightId))
        deleteSession(sessionId)
        return leftId to rightId
    }

    suspend fun merge(firstId: Long, secondId: Long): Long? {
        if (firstId == secondId) return null
        val first = sessionDao.getById(firstId) ?: return null
        val second = sessionDao.getById(secondId) ?: return null
        val mergedSamples = SessionSplitMerge.merge(
            sampleDao.getBySession(firstId),
            sampleDao.getBySession(secondId),
        )
        if (mergedSamples.isEmpty()) return null
        val start = minOf(first.startTimeMs, second.startTimeMs)
        val end = listOfNotNull(first.endTimeMs, second.endTimeMs).maxOrNull()
        val newId = sessionDao.insert(
            first.copy(
                id = 0L,
                name = SessionSplitMerge.mergeName(first.name, second.name),
                startTimeMs = start,
                endTimeMs = end,
            ),
        )
        sampleDao.insertAll(SessionSplitMerge.remap(mergedSamples, newId))
        return newId
    }
}
