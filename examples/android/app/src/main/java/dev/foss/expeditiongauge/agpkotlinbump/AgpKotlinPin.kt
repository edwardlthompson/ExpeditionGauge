package dev.foss.expeditiongauge.agpkotlinbump

/** KB-026 pin: do not automerge AGP 9.3.x / Kotlin 2.4.10. */
object AgpKotlinPin {
    const val AGP = "9.2.1"
    const val KOTLIN = "2.4.0"
    const val KB = "KB-026"

    fun holds(agp: String, kotlin: String): Boolean = agp == AGP && kotlin == KOTLIN

    fun skipAutomerge(dependency: String): Boolean {
        val name = dependency.lowercase()
        return name.startsWith("com.android.") || name.startsWith("org.jetbrains.kotlin")
    }

    fun allowBump(agp: String, kotlin: String): Boolean =
        !holds(agp, kotlin) && false
}
