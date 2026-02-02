package ca.backyardbirds.data.region.dto

import ca.backyardbirds.domain.model.Region
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionDto(
    @SerialName("code") val code: String,
    @SerialName("name") val name: String
)

fun RegionDto.toDomain(): Region = Region(
    code = code,
    name = name
)
