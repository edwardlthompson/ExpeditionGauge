package dev.foss.expeditiongauge.car.aanight

import android.content.res.Configuration
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AaNightModeTest {
    @Test
    fun followsCarUiNightOrHostDarkFlag() {
        assertTrue(AaNightMode.fromCarUi(Configuration.UI_MODE_NIGHT_YES, carIsDark = false))
        assertTrue(AaNightMode.fromCarUi(Configuration.UI_MODE_NIGHT_NO, carIsDark = true))
        assertFalse(AaNightMode.fromCarUi(Configuration.UI_MODE_NIGHT_NO, carIsDark = false))
    }
}
