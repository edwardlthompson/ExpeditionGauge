package dev.foss.expeditiongauge.car.aanight

import android.content.res.Configuration

object AaNightMode {
    fun fromCarUi(uiMode: Int, carIsDark: Boolean): Boolean {
        val night = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return night || carIsDark
    }
}
