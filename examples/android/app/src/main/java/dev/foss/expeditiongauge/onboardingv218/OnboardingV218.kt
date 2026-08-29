package dev.foss.expeditiongauge.onboardingv218

/** Extra onboarding steps for v2.18+ privacy and offline maps. */
enum class OnboardingV218Step {
    OfflineMaps,
    PrivacyBackup,
}

object OnboardingV218 {
    fun afterLegacy(): List<OnboardingV218Step> = OnboardingV218Step.entries

    fun label(step: OnboardingV218Step): String = when (step) {
        OnboardingV218Step.OfflineMaps -> "Cache map tiles before you leave signal"
        OnboardingV218Step.PrivacyBackup -> "Export a privacy report and back up settings on-device"
    }
}
