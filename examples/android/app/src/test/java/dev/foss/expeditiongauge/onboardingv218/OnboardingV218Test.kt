package dev.foss.expeditiongauge.onboardingv218

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingV218Test {
    @Test
    fun addsOfflineMapsAndPrivacyBackup() {
        val steps = OnboardingV218.afterLegacy()
        assertEquals(2, steps.size)
        assertTrue(steps.contains(OnboardingV218Step.OfflineMaps))
        assertTrue(OnboardingV218.label(OnboardingV218Step.PrivacyBackup).contains("privacy"))
    }
}
