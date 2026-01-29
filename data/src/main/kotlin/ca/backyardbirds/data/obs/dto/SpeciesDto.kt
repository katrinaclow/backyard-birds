package ca.backyardbirds.data.obs.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ca.backyardbirds.domain.model.Species

@Serializable
data class SpeciesDto(
    @SerialName("speciesCode") val speciesCode: String,
    @SerialName("comName") val commonName: String,
    @SerialName("sciName") val scientificName: String,
    @SerialName("category") val category: String? = null,
    @SerialName("order") val order: String? = null,
    @SerialName("family") val family: String? = null
)

fun SpeciesDto.toDomain(): Species = Species(
    speciesCode = speciesCode,
    commonName = commonName,
    scientificName = scientificName,
    category = category,
    order = order,
    family = family
)
