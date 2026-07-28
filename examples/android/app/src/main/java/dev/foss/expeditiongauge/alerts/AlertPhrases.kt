package dev.foss.expeditiongauge.alerts

import android.content.Context
import dev.foss.expeditiongauge.R

object AlertPhrases {
    fun phrase(context: Context, event: AlertEvent): String {
        val res = when (event.type) {
            AlertType.SPEED -> R.string.alert_tts_overspeed
            AlertType.PITCH -> R.string.alert_tts_extreme_pitch
            AlertType.ROLL -> R.string.alert_tts_extreme_roll
            AlertType.LAT_G -> R.string.alert_tts_lat_g
            AlertType.DRIFT_ANGLE -> R.string.alert_tts_drift
            AlertType.SLIP_RATIO -> R.string.alert_tts_slip
            AlertType.RPM -> R.string.alert_tts_rpm
            AlertType.FUEL_ECONOMY -> R.string.alert_tts_fuel
            AlertType.TIRE_PRESSURE -> tirePressureRes(event.tireCorner)
            AlertType.TIRE_TEMP -> tireTempRes(event.tireCorner)
            AlertType.PRESSURE_LOSS -> tireLossRes(event.tireCorner)
        }
        return context.getString(res)
    }

    private fun tirePressureRes(corner: TireCornerId?): Int = when (corner) {
        TireCornerId.FL -> R.string.alert_tts_tire_low_fl
        TireCornerId.FR -> R.string.alert_tts_tire_low_fr
        TireCornerId.RL -> R.string.alert_tts_tire_low_rl
        TireCornerId.RR -> R.string.alert_tts_tire_low_rr
        null -> R.string.alert_tts_tire_low
    }

    private fun tireTempRes(corner: TireCornerId?): Int = when (corner) {
        TireCornerId.FL -> R.string.alert_tts_tire_hot_fl
        TireCornerId.FR -> R.string.alert_tts_tire_hot_fr
        TireCornerId.RL -> R.string.alert_tts_tire_hot_rl
        TireCornerId.RR -> R.string.alert_tts_tire_hot_rr
        null -> R.string.alert_tts_tire_hot
    }

    private fun tireLossRes(corner: TireCornerId?): Int = when (corner) {
        TireCornerId.FL -> R.string.alert_tts_tire_loss_fl
        TireCornerId.FR -> R.string.alert_tts_tire_loss_fr
        TireCornerId.RL -> R.string.alert_tts_tire_loss_rl
        TireCornerId.RR -> R.string.alert_tts_tire_loss_rr
        null -> R.string.alert_tts_tire_loss
    }
}
