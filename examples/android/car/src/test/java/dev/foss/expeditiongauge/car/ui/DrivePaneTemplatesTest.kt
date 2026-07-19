package dev.foss.expeditiongauge.car.ui

import androidx.car.app.model.CarIcon
import androidx.car.app.model.PaneTemplate
import dev.foss.expeditiongauge.car.DriveHudContent
import dev.foss.expeditiongauge.car.DriveHudRow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DrivePaneTemplatesTest {
    @Test
    fun pane_usesSpacerWhenNoStatusRows() {
        val content = DriveHudContent(
            image = CarIcon.APP_ICON,
            rows = emptyList(),
        )
        val pane = DrivePaneTemplates.pane(content)
        assertEquals(CarIcon.APP_ICON, pane.image)
        assertEquals(1, pane.rows.size)
        assertEquals(DrivePaneTemplates.SPACER, pane.rows[0].title.toString())
    }

    @Test
    fun pane_keepsAlertRow() {
        val content = DriveHudContent(
            image = CarIcon.APP_ICON,
            rows = listOf(DriveHudRow("Alert", "Pitch"), DriveHudRow("Extra", "ignored")),
        )
        val pane = DrivePaneTemplates.pane(content)
        assertEquals(1, pane.rows.size)
        assertEquals("Alert", pane.rows[0].title.toString())
    }

    @Test
    fun waitingTemplate_isPaneTemplate() {
        val t = DrivePaneTemplates.waitingTemplate("Open ExpeditionGauge on phone")
        assertTrue(t is PaneTemplate)
        assertNotNull((t as PaneTemplate).pane.image)
    }
}
