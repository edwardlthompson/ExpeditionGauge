package dev.foss.expeditiongauge.wettire

import dev.foss.expeditiongauge.alerts.AlertThresholds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WetTireAlertsTest {
    @Test
    fun tightensTireLimitsOnlyWhenWet() {
        val dry = AlertThresholds(minTirePressureKpa = 200f, maxTireTempC = 80f, rapidPressureLossKpaPerMin = 20f)
        assertEquals(dry, WetTireAlerts.apply(dry, wet = false))
        val wet = WetTireAlerts.apply(dry, wet = true)
        assertEquals(220f, wet.minTirePressureKpa!!, 0.01f)
        assertEquals(70f, wet.maxTireTempC!!, 0.01f)
        assertEquals(16f, wet.rapidPressureLossKpaPerMin!!, 0.01f)
        assertNull(WetTireAlerts.apply(AlertThresholds(), wet = true).minTirePressureKpa)
    }
}
