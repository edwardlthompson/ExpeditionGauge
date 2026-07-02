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
        SettingsPreferences(context).setAttitudeGaugeMode(AttitudeGaugeMode.ATTITUDE)
    }

    @Test
    fun persistsInclinometerMode() = runBlocking {
        val prefs = SettingsPreferences(context)
        prefs.setAttitudeGaugeMode(AttitudeGaugeMode.INCLINOMETER)
        assertEquals(AttitudeGaugeMode.INCLINOMETER, prefs.attitudeGaugeMode.first())
    }
}
