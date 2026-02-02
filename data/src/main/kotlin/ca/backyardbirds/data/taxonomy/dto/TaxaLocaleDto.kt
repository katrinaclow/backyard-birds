package ca.backyardbirds.data.taxonomy.dto

import ca.backyardbirds.domain.model.TaxaLocale
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaxaLocaleDto(
    @SerialName("code") val code: String,
    @SerialName("name") val name: String,
    @SerialName("lastUpdate") val lastUpdate: String
)

fun TaxaLocaleDto.toDomain(): TaxaLocale = TaxaLocale(
    code = code,
    name = name,
    lastUpdate = lastUpdate
)
