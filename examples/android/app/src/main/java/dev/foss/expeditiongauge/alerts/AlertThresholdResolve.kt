package dev.foss.expeditiongauge.alerts

import dev.foss.expeditiongauge.presetalerts.PresetAlertThresholds
import dev.foss.expeditiongauge.settings.SettingsProfileRepository
import dev.foss.expeditiongauge.settings.WetTireStore
import dev.foss.expeditiongauge.wettire.WetTireAlerts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal fun resolvedAlertThresholds(
    thresholdsPreferences: AlertThresholdsPreferences,
    profileRepository: SettingsProfileRepository,
    wetStore: WetTireStore,
): Flow<AlertThresholds> = combine(
    thresholdsPreferences.thresholds,
    profileRepository.activeProfile,
    wetStore.enabled,
) { global, profile, wet ->
    WetTireAlerts.apply(PresetAlertThresholds.resolve(profile.presetId, global), wet)
}
