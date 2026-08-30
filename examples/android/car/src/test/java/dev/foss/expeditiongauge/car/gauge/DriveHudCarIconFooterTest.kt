package dev.foss.expeditiongauge.car.gauge

import dev.foss.expeditiongauge.car.HudStripOrientation
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DriveHudCarIconFooterTest {
    @Test
    fun renderBitmap_rowAlwaysReservesFooterBand() {
        val cube = 280
        val footer = DriveHudStripMetrics.footerPxFor(cube)
        val empty = DriveHudCarIcon.renderBitmap(
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
            cubePx = cube,
        )
        assertEquals(cube * 3, empty.width)
        assertEquals(cube + footer, empty.height)

        val withDtc = DriveHudCarIcon.renderBitmap(
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
            cubePx = cube,
            dtcFooterLine = "1/2  P0420 Catalyst",
        )
        assertEquals(cube + footer, withDtc.height)
    }

    @Test
    fun renderBitmap_columnIgnoresDtcFooter() {
        val cube = 280
        val bmp = DriveHudCarIcon.renderBitmap(
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
            cubePx = cube,
            orientation = HudStripOrientation.COLUMN,
            dtcFooterLine = "1/1  P0420 Catalyst",
        )
        assertEquals(cube, bmp.width)
        assertEquals(cube * 2, bmp.height)
    }
}
