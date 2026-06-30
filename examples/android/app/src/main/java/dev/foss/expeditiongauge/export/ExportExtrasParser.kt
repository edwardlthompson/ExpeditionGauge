package dev.foss.expeditiongauge.export

data class TpmsCornerExport(
    val pressureKpa: Float? = null,
    val tempC: Float? = null,
)

data class TpmsExportColumns(
    val frontLeft: TpmsCornerExport = TpmsCornerExport(),
    val frontRight: TpmsCornerExport = TpmsCornerExport(),
    val rearLeft: TpmsCornerExport = TpmsCornerExport(),
    val rearRight: TpmsCornerExport = TpmsCornerExport(),
) {
    val hasAnyData: Boolean
        get() = listOf(frontLeft, frontRight, rearLeft, rearRight).any {
            it.pressureKpa != null || it.tempC != null
        }
}

object ExportExtrasParser {
    fun tpmsColumns(extrasJson: String?): TpmsExportColumns {
        val json = extrasJson ?: return TpmsExportColumns()
        return TpmsExportColumns(
            frontLeft = corner(json, "fl"),
            frontRight = corner(json, "fr"),
            rearLeft = corner(json, "rl"),
            rearRight = corner(json, "rr"),
        )
    }

    fun sessionHasTpms(samples: List<dev.foss.expeditiongauge.data.db.entities.SampleEntity>): Boolean =
        samples.any { tpmsColumns(it.extrasJson).hasAnyData }

    private fun corner(json: String, key: String): TpmsCornerExport {
        val block = """"$key"\s*:\s*\{([^}]*)\}""".toRegex().find(json)?.groupValues?.get(1) ?: return TpmsCornerExport()
        return TpmsCornerExport(
            pressureKpa = floatField(block, "pressureKpa"),
            tempC = floatField(block, "tempC"),
        )
    }

    private fun floatField(block: String, key: String): Float? =
        """"$key"\s*:\s*([-\d.]+)""".toRegex().find(block)?.groupValues?.get(1)?.toFloatOrNull()
}
