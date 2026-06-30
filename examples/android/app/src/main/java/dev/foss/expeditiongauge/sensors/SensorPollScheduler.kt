package dev.foss.expeditiongauge.sensors

import android.hardware.SensorManager

/**
 * Phone-only default poll rates — see `docs/THERMAL_PERFORMANCE.md`.
 * External BLE/OBD rates are owned by their respective managers.
 */
object SensorPollScheduler {
    /** ~50 Hz IMU fusion (SENSOR_DELAY_GAME). */
    val phoneImuSensorDelay: Int = SensorManager.SENSOR_DELAY_GAME

    /** 2 Hz phone GPS (LocationManager min time). */
    const val PHONE_GPS_INTERVAL_MS: Long = 500L

    /** Default RecordingWriter sample interval. */
    const val DEFAULT_LOG_INTERVAL_MS: Long = 20L
}
