package ca.backyardbirds.features.api_explorer.model

import kotlinx.serialization.Serializable

/**
 * Simplified species result for autocomplete
 */
@Serializable
data class SpeciesSearchResult(
    val speciesCode: String,
    val commonName: String,
    val scientificName: String,
    val category: String
)

/**
 * Simplified region result for autocomplete
 */
@Serializable
data class RegionSearchResult(
    val code: String,
    val name: String,
    val regionType: String?
)
