package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TaxonomyEntry(
    val speciesCode: String,
    val commonName: String,
    val scientificName: String,
    val category: String,
    val taxonOrder: Double,
    val bandingCodes: List<String>,
    val comNameCodes: List<String>,
    val sciNameCodes: List<String>,
    val order: String?,
    val familyCode: String?,
    val familyComName: String?,
    val familySciName: String?
)
