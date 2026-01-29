package ca.backyardbirds.domain.model

data class Observation(
    val speciesCode: String,
    val commonName: String,
    val scientificName: String,
    val locationId: String,
    val locationName: String,
    val observationDate: String, // Consider using java.time.LocalDateTime for advanced use
    val howMany: Int?,
    val latitude: Double,
    val longitude: Double,
    val isValid: Boolean,
    val isReviewed: Boolean,
    val isLocationPrivate: Boolean,
    val submissionId: String
)
