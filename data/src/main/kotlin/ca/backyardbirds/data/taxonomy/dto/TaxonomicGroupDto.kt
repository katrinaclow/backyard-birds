package ca.backyardbirds.data.taxonomy.dto

import ca.backyardbirds.domain.model.TaxonomicGroup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaxonomicGroupDto(
    @SerialName("groupName") val groupName: String,
    @SerialName("groupOrder") val groupOrder: Int,
    @SerialName("taxonOrderBounds") val taxonOrderBounds: List<List<Double>>
)

fun TaxonomicGroupDto.toDomain(): TaxonomicGroup = TaxonomicGroup(
    groupName = groupName,
    groupOrder = groupOrder,
    taxonOrderBounds = taxonOrderBounds
)
