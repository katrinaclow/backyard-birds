package ca.backyardbirds.domain.query

/**
 * Query parameters for region info endpoint.
 * All parameters are optional and use sensible defaults on the eBird API side.
 *
 * @property regionNameFormat Format for region name: "detailed", "detailednoqual", "full", "namequal", "nameonly", "revdetailed"
 */
data class RegionInfoQueryParams(
    val regionNameFormat: String? = null
) {
    companion object {
        val DEFAULT = RegionInfoQueryParams()
    }
}
