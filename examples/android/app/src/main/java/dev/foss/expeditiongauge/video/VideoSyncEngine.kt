package dev.foss.expeditiongauge.video

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dev.foss.expeditiongauge.data.db.dao.RecordingSessionDao
import dev.foss.expeditiongauge.settings.DualDashcamStore

interface VideoSyncEngine {
    val isSupported: Boolean
    val videoUri: String?
    val videoOffsetMs: Long
    val boundSessionId: Long?

    fun attachPlayer(player: ExoPlayer)
    suspend fun bindSession(sessionId: Long)
    suspend fun importVideo(uri: String): Result<Unit>
    suspend fun setOffsetMs(offsetMs: Long)
    fun seekVideoToPlaybackPosition(playbackPositionMs: Long)
    fun release()
}

class DefaultVideoSyncEngine(
    private val context: Context,
    private val sessionDao: RecordingSessionDao,
) : VideoSyncEngine {
    private var player: ExoPlayer? = null
    private var sessionId: Long? = null
    private var uri: String? = null
    private var offsetMs: Long = 0L

    override val isSupported: Boolean = true
    override val videoUri: String? get() = uri
    override val videoOffsetMs: Long get() = offsetMs
    override val boundSessionId: Long? get() = sessionId

    override fun attachPlayer(player: ExoPlayer) {
        this.player = player
        uri?.let { loadIntoPlayer(it) }
    }

    override suspend fun bindSession(sessionId: Long) {
        this.sessionId = sessionId
        val session = sessionDao.getById(sessionId) ?: return
        uri = session.videoUri
        offsetMs = session.videoOffsetMs
        uri?.let { loadIntoPlayer(it) }
    }

    override suspend fun importVideo(uri: String): Result<Unit> = runCatching {
        val id = sessionId ?: error("No session bound")
        val session = sessionDao.getById(id) ?: error("Session $id not found")
        if (!session.videoUri.isNullOrBlank() && session.videoUri != uri) {
            DualDashcamStore(context).add(id, uri)
            return@runCatching
        }
        sessionDao.update(session.copy(videoUri = uri, videoOffsetMs = offsetMs))
        this.uri = uri
        loadIntoPlayer(uri)
    }

    override suspend fun setOffsetMs(offsetMs: Long) {
        this.offsetMs = offsetMs
        val id = sessionId ?: return
        val session = sessionDao.getById(id) ?: return
        sessionDao.update(session.copy(videoOffsetMs = offsetMs))
    }

    override fun seekVideoToPlaybackPosition(playbackPositionMs: Long) {
        val p = player ?: return
        if (uri == null) return
        val target = (playbackPositionMs + offsetMs).coerceAtLeast(0L)
        if (kotlin.math.abs(p.currentPosition - target) > 200L) {
            p.seekTo(target)
        }
        if (p.playbackState == Player.STATE_IDLE) {
            p.prepare()
        }
    }

    override fun release() {
        player?.release()
        player = null
    }

    private fun loadIntoPlayer(uriString: String) {
        val p = player ?: return
        p.setMediaItem(MediaItem.fromUri(Uri.parse(uriString)))
        p.prepare()
        p.pause()
    }
}

class VideoSyncEngineStub : VideoSyncEngine {
    override val isSupported: Boolean = false
    override val videoUri: String? = null
    override val videoOffsetMs: Long = 0L
    override val boundSessionId: Long? = null

    override fun attachPlayer(player: ExoPlayer) = Unit
    override suspend fun bindSession(sessionId: Long) = Unit
    override suspend fun importVideo(uri: String) =
        Result.failure<Unit>(UnsupportedOperationException("Video sync disabled"))

    override suspend fun setOffsetMs(offsetMs: Long) = Unit
    override fun seekVideoToPlaybackPosition(playbackPositionMs: Long) = Unit
    override fun release() = Unit
}
