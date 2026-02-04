package ca.backyardbirds.domain.query

/**
 * Query parameters for historic observation endpoints.
 * All parameters are optional and use sensible defaults on the eBird API side.
 *
 * @property rank Return observations ranked by "mrec" (most recent) or "create" (first reported)
 * @property detail Detail level: "simple" or "full" (default: simple)
 * @property hotspot Only return observations from hotspots (default: false)
 * @property includeProvisional Include observations not yet reviewed (default: false)
 * @property maxResults Maximum number of observations to return (1-10000)
 * @property sppLocale Language/locale for species common names (default: "en")
 * @property cat Species category filter (species, slash, spuh, hybrid, domestic, form, issf, intergrade)
 */
data class HistoricQueryParams(
    val rank: String? = null,
    val detail: String? = null,
    val hotspot: Boolean? = null,
    val includeProvisional: Boolean? = null,
    val maxResults: Int? = null,
    val sppLocale: String? = null,
    val cat: String? = null
) {
    companion object {
        val DEFAULT = HistoricQueryParams()
    }
}
