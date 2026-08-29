package dev.foss.expeditiongauge.githubfeedback

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object IssueSearch {
    const val COOLDOWN_MS = 60_000L
    private val titleField = Regex("\"title\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"")

    data class Outcome(val titles: List<String>, val fetched: Boolean)

    fun search(
        repo: String,
        fingerprint: String,
        nowMs: Long,
        lastFetchMs: Long?,
        getHttps: (String) -> String,
    ): Outcome {
        if (IssueFormUrl.isPlaceholderRepo(repo)) {
            return Outcome(emptyList(), fetched = false)
        }
        if (lastFetchMs != null && nowMs - lastFetchMs < COOLDOWN_MS) {
            return Outcome(emptyList(), fetched = false)
        }
        val q = URLEncoder.encode(
            "repo:${repo.trim()} ${fingerprint.trim()} type:issue",
            StandardCharsets.UTF_8,
        )
        val url = "https://api.github.com/search/issues?q=$q"
        val body = try {
            getHttps(url)
        } catch (_: Exception) {
            return Outcome(emptyList(), fetched = true)
        }
        return Outcome(parseTitles(body), fetched = true)
    }

    fun parseTitles(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return titleField.findAll(json).map { match ->
            match.groupValues[1].replace("\\\"", "\"")
        }.filter { it.isNotBlank() }.toList()
    }
}
