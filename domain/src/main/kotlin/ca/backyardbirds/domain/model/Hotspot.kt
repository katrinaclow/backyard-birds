package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Hotspot(
    val locId: String,
    val locName: String,
    val countryCode: String,
    val subnational1Code: String,
    val subnational2Code: String?,
    val lat: Double,
    val lng: Double,
    val latestObsDt: String?,
    val numSpeciesAllTime: Int?
)
