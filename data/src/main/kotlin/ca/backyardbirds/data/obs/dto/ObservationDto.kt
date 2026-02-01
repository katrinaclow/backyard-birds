package ca.backyardbirds.data.obs.dto

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ca.backyardbirds.domain.model.Observation

@Serializable
data class ObservationDto(
    @SerialName("speciesCode") val speciesCode: String,
    @SerialName("comName") val commonName: String,
    @SerialName("sciName") val scientificName: String,
    @SerialName("locId") val locationId: String,
    @SerialName("locName") val locationName: String,
    @SerialName("obsDt") val observationDate: String,
    @SerialName("howMany") val howMany: Int? = null,
    @SerialName("lat") val latitude: Double,
    @SerialName("lng") val longitude: Double,
    @SerialName("obsValid") val isValid: Boolean,
    @SerialName("obsReviewed") val isReviewed: Boolean,
    @SerialName("locationPrivate") val isLocationPrivate: Boolean,
    @SerialName("subId") val submissionId: String
)

// Mapper function
fun ObservationDto.toDomain(): Observation = Observation(
    speciesCode = speciesCode,
    commonName = commonName,
    scientificName = scientificName,
    locationId = locationId,
    locationName = locationName,
    observationDate = if (observationDate.length > 10)
        LocalDateTime.parse(observationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    else
        LocalDate.parse(observationDate).atStartOfDay(),
    howMany = howMany,
    latitude = latitude,
    longitude = longitude,
    isValid = isValid,
    isReviewed = isReviewed,
    isLocationPrivate = isLocationPrivate,
    submissionId = submissionId
)
