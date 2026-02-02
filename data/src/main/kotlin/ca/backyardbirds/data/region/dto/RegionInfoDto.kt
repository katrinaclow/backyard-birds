package ca.backyardbirds.data.region.dto

import ca.backyardbirds.domain.model.RegionBounds
import ca.backyardbirds.domain.model.RegionInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionInfoDto(
    @SerialName("code") val code: String,
    @SerialName("name") val name: String,
    @SerialName("bounds") val bounds: RegionBoundsDto? = null
)

@Serializable
data class RegionBoundsDto(
    @SerialName("minX") val minX: Double,
    @SerialName("maxX") val maxX: Double,
    @SerialName("minY") val minY: Double,
    @SerialName("maxY") val maxY: Double
)

fun RegionBoundsDto.toDomain(): RegionBounds = RegionBounds(
    minX = minX,
    maxX = maxX,
    minY = minY,
    maxY = maxY
)

fun RegionInfoDto.toDomain(): RegionInfo = RegionInfo(
    code = code,
    name = name,
    bounds = bounds?.toDomain()
)
