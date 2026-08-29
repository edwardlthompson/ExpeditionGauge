package dev.foss.expeditiongauge.localesesdefr

/** First product locales besides English. */
object LocalesEsDeFr {
    val TAGS = listOf("es", "de", "fr")

    fun supported(tag: String): Boolean = tag.lowercase() in TAGS
}
