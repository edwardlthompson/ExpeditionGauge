package dev.foss.expeditiongauge.phoneimulive

import dev.foss.expeditiongauge.live.LiveSampleDto

data class PhoneImu(val pitchDeg: Float, val rollDeg: Float, val headingDeg: Float)

/** Second-phone IMU carried on the live channel as imu|pitch|roll|hdg. */
object PhoneImuLive {
    fun encode(pitchDeg: Float, rollDeg: Float, headingDeg: Float): String =
        "imu|$pitchDeg|$rollDeg|$headingDeg"

    fun decode(payload: String): PhoneImu? {
        val parts = payload.trim().split('|')
        if (parts.size != 4 || parts[0] != "imu") return null
        return runCatching {
            PhoneImu(parts[1].toFloat(), parts[2].toFloat(), parts[3].toFloat())
        }.getOrNull()
    }

    fun merge(primary: LiveSampleDto, imu: PhoneImu): LiveSampleDto =
        primary.copy(pitchDeg = imu.pitchDeg, rollDeg = imu.rollDeg, headingDeg = imu.headingDeg)
}
