package dev.foss.expeditiongauge.ble

data class WitMotionSample(
    val axG: Float,
    val ayG: Float,
    val azG: Float,
    val gxDegPerSec: Float,
    val gyDegPerSec: Float,
    val gzDegPerSec: Float,
    val rollDeg: Float,
    val pitchDeg: Float,
    val yawDeg: Float,
)

/**
 * Decodes WitMotion WT901BLECL 0x61 notify packets (acc + gyro + angle).
 */
object WitMotionParser {
    private const val HEADER_0 = 0x55.toByte()
    private const val PACKET_0X61 = 0x61.toByte()

    fun parsePacket(data: ByteArray): WitMotionSample? {
        if (data.size < 20) return null
        if (data[0] != HEADER_0 || data[1] != PACKET_0X61) return null
        val ax = readInt16(data, 2) / 32768f * 16f
        val ay = readInt16(data, 4) / 32768f * 16f
        val az = readInt16(data, 6) / 32768f * 16f
        val gx = readInt16(data, 8) / 32768f * 2000f
        val gy = readInt16(data, 10) / 32768f * 2000f
        val gz = readInt16(data, 12) / 32768f * 2000f
        val roll = readInt16(data, 14) / 32768f * 180f
        val pitch = readInt16(data, 16) / 32768f * 180f
        val yaw = readInt16(data, 18) / 32768f * 180f
        return WitMotionSample(ax, ay, az, gx, gy, gz, roll, pitch, yaw)
    }

    fun buildRateCommand(hz: Int): ByteArray {
        val rateCode = when {
            hz >= 100 -> 0x09
            hz >= 50 -> 0x08
            hz >= 20 -> 0x06
            else -> 0x04
        }
        return byteArrayOf(0xFF.toByte(), 0xAA.toByte(), 0x03, rateCode.toByte(), 0x00)
    }

    private fun readInt16(data: ByteArray, offset: Int): Int {
        val low = data[offset].toInt() and 0xFF
        val high = data[offset + 1].toInt()
        return (high shl 8) or low
    }
}
