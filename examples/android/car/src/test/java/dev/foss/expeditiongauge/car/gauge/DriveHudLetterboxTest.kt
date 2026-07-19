package dev.foss.expeditiongauge.car.gauge

import android.graphics.Bitmap
import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DriveHudLetterboxTest {
    @Test
    fun toSquare_letterboxesThreeByOne() {
        val strip = Bitmap.createBitmap(300, 100, Bitmap.Config.ARGB_8888)
        strip.eraseColor(Color.RED)
        val square = DriveHudLetterbox.toSquare(strip, darkBackground = true)
        assertEquals(300, square.width)
        assertEquals(300, square.height)
    }
}
