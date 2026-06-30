package dev.foss.expeditiongauge.ble.tpms

/**
 * v2 stub — PECHAM external sensors require GATT (see KreAch3R/tpms-oap).
 */
class PechamTpmsParser : TpmsParser {
    override val parserId: String = "pecham"

    override fun canParse(name: String?, manufacturerData: ByteArray?): Boolean = false

    override fun parse(name: String?, manufacturerData: ByteArray?): TpmsReading? = null
}
