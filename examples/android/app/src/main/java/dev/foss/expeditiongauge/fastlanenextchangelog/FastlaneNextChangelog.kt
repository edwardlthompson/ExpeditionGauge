package dev.foss.expeditiongauge.fastlanenextchangelog

/** Next Play/F-Droid versionCode changelog (current app versionCode is 52). */
object FastlaneNextChangelog {
    const val CURRENT_CODE = 52
    const val NEXT_CODE = 53

    fun fileName(code: Int = NEXT_CODE): String = "$code.txt"

    fun isNext(code: Int): Boolean = code == NEXT_CODE && code == CURRENT_CODE + 1
}
