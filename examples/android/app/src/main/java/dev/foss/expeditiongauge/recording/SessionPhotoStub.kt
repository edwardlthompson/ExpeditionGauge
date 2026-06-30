package dev.foss.expeditiongauge.recording

/**
 * Stub for CameraX / TakePicture contract — returns a local file URI placeholder.
 * Hardware capture requires [ADB] validation.
 */
object SessionPhotoStub {
    fun placeholderUri(sessionId: Long): String =
        "content://dev.foss.expeditiongauge/sessions/$sessionId/photo.jpg"

    fun isStubUri(uri: String?): Boolean =
        uri?.startsWith("content://dev.foss.expeditiongauge/sessions/") == true
}
