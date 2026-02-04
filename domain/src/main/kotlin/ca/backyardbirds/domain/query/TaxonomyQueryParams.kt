package ca.backyardbirds.domain.query

/**
 * Query parameters for taxonomy endpoints.
 * All parameters are optional and use sensible defaults on the eBird API side.
 *
 * @property locale Language/locale for species common names (default: "en")
 * @property version Taxonomy version to use (e.g., "2024")
 */
data class TaxonomyQueryParams(
    val locale: String? = null,
    val version: String? = null
) {
    companion object {
        val DEFAULT = TaxonomyQueryParams()
    }
}
