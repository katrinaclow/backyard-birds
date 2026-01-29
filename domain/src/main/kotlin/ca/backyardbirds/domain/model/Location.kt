package ca.backyardbirds.domain.model

data class Location(
    val locationId: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val regionCode: String? = null,
    val country: String? = null,
    val subnational1: String? = null,
    val subnational2: String? = null
)
