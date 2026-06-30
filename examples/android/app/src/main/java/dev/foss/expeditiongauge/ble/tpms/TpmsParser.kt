package dev.foss.expeditiongauge.ble.tpms

import dev.foss.expeditiongauge.ble.ImuPlacement

data class TpmsReading(
    val macAddress: String,
    val pressureKpa: Float,
    val tempC: Float,
    val batteryVolts: Float,
    val absolutePressureKpa: Float,
    val timestampMs: Long = System.currentTimeMillis(),
)

interface TpmsParser {
    val parserId: String
    fun canParse(name: String?, manufacturerData: ByteArray?): Boolean
    fun parse(name: String?, manufacturerData: ByteArray?): TpmsReading?
}
