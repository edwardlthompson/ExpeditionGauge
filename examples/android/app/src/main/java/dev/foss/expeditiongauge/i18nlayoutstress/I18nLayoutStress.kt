package dev.foss.expeditiongauge.i18nlayoutstress

/** Flag translations that will overflow compact HUD/settings rows. */
object I18nLayoutStress {
    const val HUD_MAX = 18
    const val ROW_MAX = 28

    fun overflows(text: String, maxChars: Int = ROW_MAX): Boolean = text.length > maxChars

    fun longest(texts: List<String>): String = texts.maxByOrNull { it.length }.orEmpty()
}
