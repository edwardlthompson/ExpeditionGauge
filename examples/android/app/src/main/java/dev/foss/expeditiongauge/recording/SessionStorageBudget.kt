package dev.foss.expeditiongauge.recording

import android.content.Context
import android.os.Environment
import android.os.StatFs
import dev.foss.expeditiongauge.data.db.dao.RecordingSessionDao
import dev.foss.expeditiongauge.media.SessionDeleteService
import dev.foss.expeditiongauge.media.SessionMediaRepository
import dev.foss.expeditiongauge.settings.SettingsPreferences
import dev.foss.expeditiongauge.storageautodelete.StorageAutoDelete
import kotlinx.coroutines.flow.first
import java.io.File

class SessionStorageBudget(
    private val context: Context,
    private val sessionDao: RecordingSessionDao,
    private val sessionDeleteService: SessionDeleteService,
    private val mediaRepository: SessionMediaRepository,
    private val settingsPreferences: SettingsPreferences,
) {
    suspend fun allowedBytes(): Long {
        val percent = settingsPreferences.sessionStorageFreePercent.first().coerceIn(MIN_PERCENT, MAX_PERCENT)
        val stat = StatFs(Environment.getDataDirectory().path)
        return stat.availableBytes * percent / 100L
    }

    suspend fun usedBytes(): Long {
        val dbFile = context.getDatabasePath("expedition_gauge.db")
        val dbSize = if (dbFile.exists()) dbFile.length() else 0L
        val sessionsDir = File(context.filesDir, "sessions")
        val exportsDir = File(context.cacheDir, "exports")
        return dbSize + directorySize(sessionsDir) + directorySize(exportsDir) + mediaRepository.totalStorageBytes()
    }

    suspend fun isOverCap(): Boolean = StorageAutoDelete.needsPrune(usedBytes(), allowedBytes())

    suspend fun pruneOldestUntilUnderCap(excludeSessionId: Long? = null): Boolean {
        var pruned = false
        while (isOverCap()) {
            val victim = sessionDao.oldestUnprotectedSession(excludeSessionId) ?: return pruned
            sessionDeleteService.deleteSession(victim.id)
            pruned = true
        }
        return pruned
    }

    /**
     * @return true if space is available for a new session; false when all sessions are protected and cap exceeded.
     */
    suspend fun ensureSpaceForNewSession(): Boolean {
        pruneOldestUntilUnderCap(excludeSessionId = null)
        if (!isOverCap()) return true
        val hasUnprotected = sessionDao.oldestUnprotectedSession(null) != null
        return if (hasUnprotected) {
            pruneOldestUntilUnderCap(null)
            !isOverCap()
        } else {
            false
        }
    }

    private fun directorySize(dir: File): Long {
        if (!dir.exists()) return 0L
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    companion object {
        const val MIN_PERCENT = 5
        const val MAX_PERCENT = 90
        const val DEFAULT_PERCENT = 25
    }
}

class StorageCapBlockedException : Exception("Session storage cap reached; unprotect or delete a session.")
