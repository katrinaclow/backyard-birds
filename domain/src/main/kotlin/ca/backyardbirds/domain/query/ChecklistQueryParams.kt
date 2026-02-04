package ca.backyardbirds.domain.query

/**
 * Query parameters for checklist feed endpoints.
 * All parameters are optional and use sensible defaults on the eBird API side.
 *
 * @property sortKey Sort order: "obs_dt" (observation date) or "creation_dt" (creation date)
 * @property maxResults Maximum number of checklists to return (1-200, default: 10)
 */
data class ChecklistQueryParams(
    val sortKey: String? = null,
    val maxResults: Int? = null
) {
    companion object {
        val DEFAULT = ChecklistQueryParams()
    }
}
