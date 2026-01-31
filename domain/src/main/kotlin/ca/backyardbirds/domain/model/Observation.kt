package ca.backyardbirds.domain.model

import java.time.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Observation(
    val speciesCode: String,
    val commonName: String,
    val scientificName: String,
    val locationId: String,
    val locationName: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val observationDate: LocalDateTime,
    val howMany: Int?,
    val latitude: Double,
    val longitude: Double,
    val isValid: Boolean,
    val isReviewed: Boolean,
    val isLocationPrivate: Boolean,
    val submissionId: String
)
