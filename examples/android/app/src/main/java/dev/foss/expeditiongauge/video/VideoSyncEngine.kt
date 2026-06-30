package dev.foss.expeditiongauge.video

/**
 * Stub for Sprint 18 video sync — record/import, timestamp align, offset UI.
 * Extension point for MediaCodec burn-in pipeline.
 */
interface VideoSyncEngine {
    val isSupported: Boolean
    val videoOffsetMs: Long
    suspend fun importVideo(uri: String): Result<Unit>
    suspend fun setOffsetMs(offsetMs: Long)
    suspend fun seekVideoToPlaybackPosition(playbackPositionMs: Long)
}

class VideoSyncEngineStub : VideoSyncEngine {
    override val isSupported: Boolean = false
    override var videoOffsetMs: Long = 0L
        private set

    override suspend fun importVideo(uri: String): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Video sync not yet implemented"))
    }

    override suspend fun setOffsetMs(offsetMs: Long) {
        videoOffsetMs = offsetMs
    }

    override suspend fun seekVideoToPlaybackPosition(playbackPositionMs: Long) {
        // No-op until WebRTC / MediaCodec pipeline wired
    }
}
