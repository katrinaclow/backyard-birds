package ca.backyardbirds.data.obs.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ca.backyardbirds.domain.model.Location

@Serializable
data class LocationDto(
    @SerialName("locId") val locationId: String,
    @SerialName("locName") val locationName: String,
    @SerialName("lat") val latitude: Double,
    @SerialName("lng") val longitude: Double,
    @SerialName("regionCode") val regionCode: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("subnational1") val subnational1: String? = null,
    @SerialName("subnational2") val subnational2: String? = null
)

fun LocationDto.toDomain(): Location = Location(
    locationId = locationId,
    locationName = locationName,
    latitude = latitude,
    longitude = longitude,
    regionCode = regionCode,
    country = country,
    subnational1 = subnational1,
    subnational2 = subnational2
)
