package dev.foss.expeditiongauge.car

/** Maps bridge metrics to car template rows using priority order. */
data class CarMetricRow(
    val key: String,
    val title: String,
    val value: String,
)

object CarTelemetryHost {
    val defaultPriority: List<String> = listOf(
        "speed",
        "latG",
        "pitch",
        "roll",
        "beta",
        "rpm",
        "throttle",
    )

    private val titles = mapOf(
        "speed" to "Speed",
        "latG" to "Lat G",
        "pitch" to "Pitch",
        "roll" to "Roll",
        "beta" to "Drift β",
        "rpm" to "RPM",
        "throttle" to "Throttle",
    )

    fun buildRows(
        metrics: Map<String, String>,
        allowlist: Set<String>,
        priority: List<String> = defaultPriority,
    ): List<CarMetricRow> {
        return priority
            .asSequence()
            .filter { it in allowlist }
            .mapNotNull { key ->
                val value = metrics[key]?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                CarMetricRow(
                    key = key,
                    title = titles[key] ?: key,
                    value = value,
                )
            }
            .toList()
    }
}
