package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

/**
 * Writes Attitude / Telemetry / TPMS glance PNGs for Cursor review.
 * Invoked by [AaTileBitmapPreviewWriterTest] when `aa.preview.dir` is set.
 */
object AaTileBitmapPreviewWriter {
    fun writeAll(outDir: File, sizePx: Int = 256) {
        outDir.mkdirs()
        writePng(
            File(outDir, "aa-tile-attitude.png"),
            InclinometerCarIcon.renderBitmap(
                pitchDeg = 5f,
                rollDeg = 18f,
                style = InclinometerStyle.HORIZON,
                yawDeg = 90f,
                latG = 0.2f,
                lonG = 0.1f,
                sizePx = sizePx,
            ),
        )
        writePng(
            File(outDir, "aa-tile-telemetry.png"),
            AaGlanceTileIcons.renderTelemetryBitmap(
                speedLabel = "62 MPH",
                headingLabel = "HDG 090°",
                altLabel = "Alt 3940 ft",
                sizePx = sizePx,
            ),
        )
        writePng(
            File(outDir, "aa-tile-tpms.png"),
            AaGlanceTileIcons.renderTpmsBitmap(
                fl = "32\n72F",
                fr = "31\n70F",
                rl = "--",
                rr = "--",
                sizePx = sizePx,
            ),
        )
        val cube = sizePx.coerceAtLeast(280)
        writePng(
            File(outDir, "aa-drive-hud.png"),
            DriveHudCarIcon.renderBitmap(
                pitchDeg = 5f,
                rollDeg = 18f,
                style = InclinometerStyle.HORIZON,
                pitchAlert = false,
                rollAlert = false,
                maxPitchThresholdDeg = null,
                maxRollThresholdDeg = null,
                yawDeg = 90f,
                latG = 0.2f,
                lonG = 0.1f,
                speedLabel = "62 MPH",
                headingLabel = "HDG 090°",
                altLabel = "Alt 3940 ft",
                fl = "32\n72F",
                fr = "31\n70F",
                rl = "33\n68F",
                rr = "32\n69F",
                cubePx = cube,
                darkBackground = true,
            ),
        )
        writePng(
            File(outDir, "aa-drive-hud-light.png"),
            DriveHudCarIcon.renderBitmap(
                pitchDeg = 5f,
                rollDeg = 18f,
                style = InclinometerStyle.HORIZON,
                pitchAlert = false,
                rollAlert = false,
                maxPitchThresholdDeg = null,
                maxRollThresholdDeg = null,
                yawDeg = 90f,
                latG = 0.2f,
                lonG = 0.1f,
                speedLabel = "62 MPH",
                headingLabel = "HDG 090°",
                altLabel = "Alt 3940 ft",
                fl = "32\n72F",
                fr = "31\n70F",
                rl = "33\n68F",
                rr = "32\n69F",
                cubePx = cube,
                darkBackground = false,
            ),
        )
    }

    private fun writePng(file: File, bitmap: Bitmap) {
        FileOutputStream(file).use { out ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                "Failed to write ${file.name}"
            }
        }
    }
}
