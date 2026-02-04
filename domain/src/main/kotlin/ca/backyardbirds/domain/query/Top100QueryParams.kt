package ca.backyardbirds.domain.query

/**
 * Query parameters for the Top 100 statistics endpoint.
 * All parameters are optional and use sensible defaults on the eBird API side.
 *
 * @property rankedBy Ranking criteria: "spp" (species count) or "cl" (checklist count)
 * @property maxResults Maximum number of results to return (1-100, default: 100)
 */
data class Top100QueryParams(
    val rankedBy: String? = null,
    val maxResults: Int? = null
) {
    companion object {
        val DEFAULT = Top100QueryParams()
    }
}
