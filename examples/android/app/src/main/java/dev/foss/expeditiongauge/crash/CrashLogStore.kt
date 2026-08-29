package dev.foss.expeditiongauge.crash

import android.content.Context
import dev.foss.expeditiongauge.feedback.FeedbackPrefs
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant

/**
 * Local-only crash log (FOSS — no network). Caps size; no GPS/VIN/telemetry.
 */
class CrashLogStore(
    private val crashDir: File,
    private val maxBytes: Int = MAX_BYTES,
) {
    private val crashFile: File get() = File(crashDir, FILE_NAME)

    fun write(throwable: Throwable, versionLabel: String, threadName: String) {
        crashDir.mkdirs()
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val body = buildString {
            append("timestamp=").append(Instant.now()).append('\n')
            append("version=").append(versionLabel).append('\n')
            append("thread=").append(threadName).append('\n')
            append("exception=").append(throwable.javaClass.name).append('\n')
            append("message=").append(throwable.message ?: "").append('\n')
            append("---\n")
            append(sw.toString())
        }
        val truncated = if (body.length > maxBytes) {
            body.take(maxBytes) + "\n…truncated…\n"
        } else {
            body
        }
        crashFile.writeText(truncated, Charsets.UTF_8)
    }

    fun readText(): String? {
        if (!crashFile.isFile) return null
        return crashFile.readText(Charsets.UTF_8)
    }

    fun previewLines(maxLines: Int = 20): String? {
        val text = readText() ?: return null
        return text.lineSequence().take(maxLines).joinToString("\n")
    }

    fun clear() {
        if (crashFile.isFile) crashFile.delete()
    }

    fun file(): File = crashFile

    companion object {
        const val FILE_NAME = "last_crash.txt"
        const val MAX_BYTES = 64 * 1024

        fun fromContext(context: Context): CrashLogStore =
            CrashLogStore(File(context.filesDir, "crash"))
    }
}

object CrashReporter {
    @Volatile
    private var handling = false

    fun install(context: Context) {
        val app = context.applicationContext
        val store = CrashLogStore.fromContext(app)
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (!handling) {
                handling = true
                try {
                    if (FeedbackPrefs(app).saveCrashes()) {
                        val version = runCatching {
                            val info = app.packageManager.getPackageInfo(app.packageName, 0)
                            @Suppress("DEPRECATION")
                            "${info.versionName} (${info.versionCode})"
                        }.getOrDefault("unknown")
                        store.write(throwable, version, thread.name)
                        PendingCrashStore(app).write(
                            PendingCrash(
                                message = throwable.javaClass.simpleName + ": " +
                                    (throwable.message ?: ""),
                                stack = throwable.stackTraceToString(),
                            ),
                        )
                    }
                } catch (_: Throwable) {
                    // Never swallow the original crash path.
                } finally {
                    handling = false
                }
            }
            previous?.uncaughtException(thread, throwable)
        }
    }
}
