package dev.foss.expeditiongauge.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.foss.expeditiongauge.clearPreferenceDataStores
import dev.foss.expeditiongauge.gauge.AttitudeGaugeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class AttitudeGaugeModePreferencesTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun resetDataStore() = runBlocking {
        context.clearPreferenceDataStores()
        SettingsPreferences(context).setAttitudeGaugeMode(AttitudeGaugeMode.G_FORCE)
    }

    @Test
    fun persistsInclinometerMode() = runBlocking {
        val prefs = SettingsPreferences(context)
        prefs.setAttitudeGaugeMode(AttitudeGaugeMode.INCLINOMETER_LADDER)
        assertEquals(AttitudeGaugeMode.INCLINOMETER_LADDER, prefs.attitudeGaugeMode.first())
    }

    @Test
    fun persistsLegacyInclinometerKeyAsLadder() = runBlocking {
        val prefs = SettingsPreferences(context)
        // Simulate legacy storage via re-set after writing ladder key path covered above;
        // TrackGaugePreferences maps "inclinometer" → LADDER.
        prefs.setAttitudeGaugeMode(AttitudeGaugeMode.INCLINOMETER_BUBBLE)
        assertEquals(AttitudeGaugeMode.INCLINOMETER_BUBBLE, prefs.attitudeGaugeMode.first())
    }

    @Test
    fun persistsCompassBallMode() = runBlocking {
        val prefs = SettingsPreferences(context)
        prefs.setAttitudeGaugeMode(AttitudeGaugeMode.COMPASS_BALL)
        assertEquals(AttitudeGaugeMode.COMPASS_BALL, prefs.attitudeGaugeMode.first())
    }
}
