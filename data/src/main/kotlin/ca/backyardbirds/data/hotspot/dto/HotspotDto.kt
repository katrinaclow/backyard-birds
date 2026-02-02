package ca.backyardbirds.data.hotspot.dto

import ca.backyardbirds.domain.model.Hotspot
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HotspotDto(
    @SerialName("locId") val locId: String,
    @SerialName("locName") val locName: String,
    @SerialName("countryCode") val countryCode: String,
    @SerialName("subnational1Code") val subnational1Code: String,
    @SerialName("subnational2Code") val subnational2Code: String? = null,
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("latestObsDt") val latestObsDt: String? = null,
    @SerialName("numSpeciesAllTime") val numSpeciesAllTime: Int? = null
)

fun HotspotDto.toDomain(): Hotspot = Hotspot(
    locId = locId,
    locName = locName,
    countryCode = countryCode,
    subnational1Code = subnational1Code,
    subnational2Code = subnational2Code,
    lat = lat,
    lng = lng,
    latestObsDt = latestObsDt,
    numSpeciesAllTime = numSpeciesAllTime
)
