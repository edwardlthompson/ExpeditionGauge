package dev.foss.expeditiongauge.about

/** Public Venmo link — not a secret. Always available from About / Settings. */
object DonateLinks {
    const val VENMO_URL = "https://venmo.com/code?user_id=1857304970395648420"
    const val LABEL = "Donate via Venmo"

    fun isPlaceholderLink(link: DonationLink): Boolean {
        val blob = "${link.label} ${link.url}".lowercase()
        return blob.contains("[insert") || blob.contains("example.com")
    }

    fun ensureVenmo(config: DonationsConfig): DonationsConfig {
        val links = config.links.filterNot { isPlaceholderLink(it) }.toMutableList()
        if (links.none { it.url == VENMO_URL }) {
            links.add(0, DonationLink(LABEL, VENMO_URL))
        }
        return DonationsConfig(enabled = true, message = config.message, links = links)
    }
}
