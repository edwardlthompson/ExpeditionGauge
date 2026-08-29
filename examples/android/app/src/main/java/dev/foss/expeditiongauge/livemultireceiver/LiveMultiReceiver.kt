package dev.foss.expeditiongauge.livemultireceiver

/** Cap pit-room fan-out so one sender can feed several receivers. */
object LiveMultiReceiver {
    const val MAX = 8

    fun accept(count: Int, max: Int = MAX): Boolean = count in 0..max

    fun fanout(payload: String, count: Int, max: Int = MAX): List<String> {
        val n = count.coerceIn(0, max)
        return List(n) { payload }
    }

    fun label(count: Int, max: Int = MAX): String = "$count / $max"
}
