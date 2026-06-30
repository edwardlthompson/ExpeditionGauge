package dev.foss.expeditiongauge.ble.tpms

/**
 * "BR" valve-stem TPMS parser (SYTPMS-compatible).
 * Reference: omadon/TPMS_BLE_BR — Kotlin reimplementation.
 */
class BrTpmsParser(
    private val atmosphericKpa: Float = DEFAULT_ATMOSPHERIC_KPA,
) : TpmsParser {
    override val parserId: String = "br"

    override fun canParse(name: String?, manufacturerData: ByteArray?): Boolean {
        if (name?.startsWith("BR", ignoreCase = true) == true) return true
        return manufacturerData != null && manufacturerData.size >= 6
    }

    override fun parse(name: String?, manufacturerData: ByteArray?): TpmsReading? {
        val data = manufacturerData ?: return null
        if (data.size < 6) return null
        val status = data[0].toInt() and 0xFF
        val batteryVolts = (data[1].toInt() and 0xFF) / 10f
        val tempC = (data[2].toInt() and 0xFF).toFloat()
        val absolutePsi = ((data[3].toInt() and 0xFF) shl 8 or (data[4].toInt() and 0xFF)) / 10f
        val relativePsi = absolutePsi - DEFAULT_ATMOSPHERIC_PSI
        val relativeKpa = relativePsi * PSI_TO_KPA
        val macSuffix = if (data.size >= 7) {
            String.format("%02X%02X", data[5].toInt() and 0xFF, data[6].toInt() and 0xFF)
        } else {
            "unknown"
        }
        return TpmsReading(
            macAddress = macSuffix,
            pressureKpa = relativeKpa,
            tempC = tempC,
            batteryVolts = batteryVolts,
            absolutePressureKpa = absolutePsi * PSI_TO_KPA,
        )
    }

    companion object {
        const val DEFAULT_ATMOSPHERIC_KPA = 101.3f
        const val DEFAULT_ATMOSPHERIC_PSI = 14.5f
        private const val PSI_TO_KPA = 6.894757f
    }
}
