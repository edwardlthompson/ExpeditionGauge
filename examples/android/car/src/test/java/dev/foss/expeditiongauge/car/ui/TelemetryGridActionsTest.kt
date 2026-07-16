package dev.foss.expeditiongauge.car.ui

import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class TelemetryGridActionsTest {

    @Test
    fun recordTitle_flipsWithRecordingState() {
        assertEquals(TelemetryGridActions.TITLE_RECORD, TelemetryGridActions.recordTitle(false))
        assertEquals(TelemetryGridActions.TITLE_STOP, TelemetryGridActions.recordTitle(true))
    }

    @Test
    fun build_keepsAtMostOneCustomTitle_andAcceptsGridTemplate() {
        val strip = TelemetryGridActions.build(
            isRecording = false,
            recordIcon = CarIcon.APP_ICON,
            zeroIcon = CarIcon.APP_ICON,
            onRecordToggle = {},
            onZero = {},
        )

        assertEquals(2, strip.actions.size)
        assertEquals(1, TelemetryGridActions.titledActionCount(strip))
        assertEquals(TelemetryGridActions.TITLE_RECORD, strip.actions[0].title.toString())
        assertNotNull(strip.actions[0].icon)
        assertNull(strip.actions[1].title)
        assertNotNull(strip.actions[1].icon)
        assertTrue(TelemetryGridActions.isZeroParkedOnly(strip))

        GridTemplate.Builder()
            .setTitle("ExpeditionGauge")
            .setSingleList(minimalGridList())
            .setActionStrip(strip)
            .build()
    }

    @Test
    fun build_stopTitleWhenRecording() {
        val strip = TelemetryGridActions.build(
            isRecording = true,
            recordIcon = CarIcon.APP_ICON,
            zeroIcon = CarIcon.APP_ICON,
            onRecordToggle = {},
            onZero = {},
        )
        assertEquals(TelemetryGridActions.TITLE_STOP, strip.actions[0].title.toString())
        assertEquals(1, TelemetryGridActions.titledActionCount(strip))
    }

    @Test
    fun twoTitledActions_rejectedByGridTemplate() {
        val badStrip = ActionStrip.Builder()
            .addAction(Action.Builder().setTitle("Record").setOnClickListener {}.build())
            .addAction(Action.Builder().setTitle("Zero").setOnClickListener {}.build())
            .build()

        var threw = false
        try {
            GridTemplate.Builder()
                .setTitle("ExpeditionGauge")
                .setSingleList(minimalGridList())
                .setActionStrip(badStrip)
                .build()
        } catch (e: IllegalArgumentException) {
            threw = true
            assertTrue(e.message!!.contains("custom titles"))
        }
        assertTrue(threw)
    }

    private fun minimalGridList(): ItemList =
        ItemList.Builder()
            .addItem(
                GridItem.Builder()
                    .setTitle("T")
                    .setText(CarText.create("x"))
                    .setImage(CarIcon.APP_ICON, GridItem.IMAGE_TYPE_LARGE)
                    .build(),
            )
            .build()
}
