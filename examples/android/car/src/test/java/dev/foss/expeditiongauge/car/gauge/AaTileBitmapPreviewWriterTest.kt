package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Opt-in PNG export for Cursor: set `-Daa.preview.dir=<path>` (see `aa-bitmap-preview.ps1`).
 * Without the property the test is skipped so CI stays clean.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AaTileBitmapPreviewWriterTest {
    @Test
    fun writesGlancePngs_whenPreviewDirSet() {
        val dirProp = System.getProperty("aa.preview.dir")
        assumeTrue("aa.preview.dir not set — skipping PNG export", !dirProp.isNullOrBlank())
        val outDir = File(dirProp!!)
        AaTileBitmapPreviewWriter.writeAll(outDir)
        assertTrue(File(outDir, "aa-tile-attitude.png").isFile)
        assertTrue(File(outDir, "aa-tile-telemetry.png").isFile)
        assertTrue(File(outDir, "aa-tile-tpms.png").isFile)
        assertTrue(File(outDir, "aa-drive-hud.png").isFile)
    }
}
