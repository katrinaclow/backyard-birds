package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TaxonomyVersion(
    val authorityVer: Double,
    val latest: Boolean
)
