package ca.backyardbirds.domain.query

/**
 * Query parameters for observation endpoints.
 * All parameters are optional and use sensible defaults on the eBird API side.
 *
 * @property back Number of days back to look for observations (1-30, default: 14)
 * @property hotspot Only return observations from hotspots (default: false)
 * @property includeProvisional Include observations not yet reviewed (default: false)
 * @property maxResults Maximum number of observations to return (1-10000)
 * @property sppLocale Language/locale for species common names (default: "en")
 * @property cat Species category filter (species, slash, spuh, hybrid, domestic, form, issf, intergrade)
 * @property sort Sort order: "date" or "species" (default: date)
 */
data class ObservationQueryParams(
    val back: Int? = null,
    val hotspot: Boolean? = null,
    val includeProvisional: Boolean? = null,
    val maxResults: Int? = null,
    val sppLocale: String? = null,
    val cat: String? = null,
    val sort: String? = null
) {
    companion object {
        val DEFAULT = ObservationQueryParams()
    }
}
