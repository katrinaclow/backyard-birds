package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TaxonomicGroup(
    val groupName: String,
    val groupOrder: Int,
    val taxonOrderBounds: List<List<Double>>
)
