package ca.backyardbirds.data.taxonomy.dto

import ca.backyardbirds.domain.model.TaxonomyEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaxonomyEntryDto(
    @SerialName("speciesCode") val speciesCode: String,
    @SerialName("comName") val commonName: String,
    @SerialName("sciName") val scientificName: String,
    @SerialName("category") val category: String,
    @SerialName("taxonOrder") val taxonOrder: Double,
    @SerialName("bandingCodes") val bandingCodes: List<String> = emptyList(),
    @SerialName("comNameCodes") val comNameCodes: List<String> = emptyList(),
    @SerialName("sciNameCodes") val sciNameCodes: List<String> = emptyList(),
    @SerialName("order") val order: String? = null,
    @SerialName("familyCode") val familyCode: String? = null,
    @SerialName("familyComName") val familyComName: String? = null,
    @SerialName("familySciName") val familySciName: String? = null
)

fun TaxonomyEntryDto.toDomain(): TaxonomyEntry = TaxonomyEntry(
    speciesCode = speciesCode,
    commonName = commonName,
    scientificName = scientificName,
    category = category,
    taxonOrder = taxonOrder,
    bandingCodes = bandingCodes,
    comNameCodes = comNameCodes,
    sciNameCodes = sciNameCodes,
    order = order,
    familyCode = familyCode,
    familyComName = familyComName,
    familySciName = familySciName
)
