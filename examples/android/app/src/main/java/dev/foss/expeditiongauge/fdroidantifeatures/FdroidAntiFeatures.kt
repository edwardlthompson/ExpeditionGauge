package dev.foss.expeditiongauge.fdroidantifeatures

/** Declared F-Droid Anti-Features. Empty = none apply. */
object FdroidAntiFeatures {
    val declared: List<String> = emptyList()

    fun applies(name: String): Boolean = name in declared

    fun listing(): String =
        if (declared.isEmpty()) "None" else declared.joinToString("\n")
}
