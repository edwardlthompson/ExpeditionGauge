package dev.foss.expeditiongauge.fdroidreproducible

/** Required F-Droid listing files plus the reproducible-build pin. */
object FdroidReproducible {
    const val SOURCE_DATE_EPOCH = "1700000000"

    val requiredFiles = listOf(
        "title.txt",
        "short_description.txt",
        "full_description.txt",
        "license.txt",
        "source_code.txt",
    )

    fun complete(present: Set<String>): Boolean =
        requiredFiles.all { it in present }

    fun epochPinned(value: String): Boolean = value == SOURCE_DATE_EPOCH
}
