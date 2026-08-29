package dev.foss.expeditiongauge.presetalerts

import dev.foss.expeditiongauge.alerts.AlertThresholds
import dev.foss.expeditiongauge.presets.DashboardPresetId

object PresetAlertThresholds {
    fun defaults(presetId: DashboardPresetId): AlertThresholds = when (presetId) {
        DashboardPresetId.Default, DashboardPresetId.Minimal -> AlertThresholds()
        DashboardPresetId.Drift -> AlertThresholds(
            maxLatG = 0.85f,
            maxAbsDriftAngleDeg = 20f,
            maxSlipRatio = 0.25f,
        )
        DashboardPresetId.Offroad -> AlertThresholds(
            maxPitchDeg = 32f,
            maxRollDeg = 28f,
        )
        DashboardPresetId.Track -> AlertThresholds(
            maxLatG = 1.2f,
            maxRpm = 7000f,
            maxSpeedMps = 69.4f,
        )
    }

    fun resolve(presetId: DashboardPresetId, global: AlertThresholds): AlertThresholds {
        val named = defaults(presetId)
        return global.copy(
            maxLatG = global.maxLatG ?: named.maxLatG,
            maxAbsDriftAngleDeg = global.maxAbsDriftAngleDeg ?: named.maxAbsDriftAngleDeg,
            maxSlipRatio = global.maxSlipRatio ?: named.maxSlipRatio,
            maxPitchDeg = global.maxPitchDeg ?: named.maxPitchDeg,
            maxRollDeg = global.maxRollDeg ?: named.maxRollDeg,
            maxRpm = global.maxRpm ?: named.maxRpm,
            maxSpeedMps = global.maxSpeedMps ?: named.maxSpeedMps,
        )
    }
}
