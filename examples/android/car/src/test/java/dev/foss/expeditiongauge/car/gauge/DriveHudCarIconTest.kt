package dev.foss.expeditiongauge.car.gauge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DriveHudCarIconTest {
    @Test
    fun renderBitmap_isNativeThreeByOne() {
        val cube = 280
        val bmp = DriveHudCarIcon.renderBitmap(
            pitchDeg = 5f,
            rollDeg = 10f,
            attitudeMode = AaAttitudeMode.INCLINOMETER_HORIZON,
            pitchAlert = false,
            rollAlert = false,
            maxPitchThresholdDeg = null,
            maxRollThresholdDeg = null,
            yawDeg = 90f,
            latG = 0.1f,
            lonG = 0f,
            speedLabel = "62 MPH",
            headingLabel = "HDG 090° E",
            altLabel = "Alt 1000 ft",
            coordsLabel = "18.4573°N 66.1846°W",
            fl = "32\n72F",
            fr = "31\n70F",
            rl = "--",
            rr = "--",
            cubePx = cube,
            darkBackground = true,
        )
        assertEquals(cube * 3, bmp.width)
        assertEquals(cube, bmp.height)
        assertTrue(bmp.config == android.graphics.Bitmap.Config.ARGB_8888)
    }

    @Test
    fun renderBitmap_gMeterAndCompassModes() {
        for (mode in listOf(AaAttitudeMode.G_FORCE, AaAttitudeMode.COMPASS_BALL)) {
            val bmp = DriveHudCarIcon.renderBitmap(
                pitchDeg = 2f,
                rollDeg = -1f,
                attitudeMode = mode,
                pitchAlert = false,
                rollAlert = false,
                maxPitchThresholdDeg = null,
                maxRollThresholdDeg = null,
                yawDeg = 45f,
                latG = null,
                lonG = null,
                speedLabel = "0 KM/H",
                headingLabel = "HDG 000° N",
                altLabel = "Alt —",
                fl = "--",
                fr = "--",
                rl = "--",
                rr = "--",
                cubePx = 200,
            )
            assertEquals(600, bmp.width)
            assertEquals(200, bmp.height)
        }
    }

    @Test
    fun from_buildsCarIcon() {
        assertNotNull(
            DriveHudCarIcon.from(
                pitchDeg = 0f,
                rollDeg = 0f,
                attitudeMode = AaAttitudeMode.INCLINOMETER_LADDER,
                pitchAlert = false,
                rollAlert = false,
                maxPitchThresholdDeg = null,
                maxRollThresholdDeg = null,
                yawDeg = null,
                latG = null,
                lonG = null,
                speedLabel = "0 KM/H",
                headingLabel = "HDG 000° N",
                altLabel = "Alt —",
                fl = "--",
                fr = "--",
                rl = "--",
                rr = "--",
                cubePx = 200,
                darkBackground = false,
            ),
        )
    }
}
