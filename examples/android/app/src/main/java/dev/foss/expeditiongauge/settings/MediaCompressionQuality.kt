package dev.foss.expeditiongauge.settings

enum class MediaCompressionQuality(val jpegQuality: Int) {
    ORIGINAL(100),
    BALANCED(85),
    COMPACT(70),
}
