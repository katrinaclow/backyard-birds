package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TaxaLocale(
    val code: String,
    val name: String,
    val lastUpdate: String
)
