package ca.backyardbirds.data.taxonomy.dto

import ca.backyardbirds.domain.model.TaxonomyVersion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaxonomyVersionDto(
    @SerialName("authorityVer") val authorityVer: Double,
    @SerialName("latest") val latest: Boolean
)

fun TaxonomyVersionDto.toDomain(): TaxonomyVersion = TaxonomyVersion(
    authorityVer = authorityVer,
    latest = latest
)
