package dev.foss.expeditiongauge.gpxbeta

/** Namespaced GPX extensions for β, latG, and lonG. */
object GpxBetaExtensions {
    const val PREFIX = "eg"
    const val NS = "https://foss.dev/expeditiongauge/gpx/1"

    fun xmlnsAttr(): String = "xmlns:$PREFIX=\"$NS\""

    fun tags(latG: Float?, lonG: Float?, betaDeg: Float?): String = buildString {
        latG?.let { append("          <$PREFIX:latG>$it</$PREFIX:latG>\n") }
        lonG?.let { append("          <$PREFIX:lonG>$it</$PREFIX:lonG>\n") }
        betaDeg?.let { append("          <$PREFIX:betaDeg>$it</$PREFIX:betaDeg>\n") }
    }
}
