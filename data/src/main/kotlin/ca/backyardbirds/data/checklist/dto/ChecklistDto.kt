package ca.backyardbirds.data.checklist.dto

import ca.backyardbirds.domain.model.Checklist
import ca.backyardbirds.domain.model.ChecklistObservation
import ca.backyardbirds.domain.model.ChecklistSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class ChecklistSummaryDto(
    @SerialName("subId") val subId: String,
    @SerialName("locId") val locId: String,
    @SerialName("userDisplayName") val userDisplayName: String,
    @SerialName("numSpecies") val numSpecies: Int,
    @SerialName("obsDt") val obsDt: String
)

@Serializable
data class ChecklistDto(
    @SerialName("subId") val subId: String,
    @SerialName("locId") val locId: String,
    @SerialName("userDisplayName") val userDisplayName: String,
    @SerialName("numSpecies") val numSpecies: Int,
    @SerialName("obsDt") val obsDt: String,
    @SerialName("obs") val obs: List<ChecklistObservationDto>
)

@Serializable
data class ChecklistObservationDto(
    @SerialName("speciesCode") val speciesCode: String,
    @SerialName("howManyStr") val howManyStr: String? = null
)

private fun parseObsDate(obsDt: String): LocalDateTime =
    if (obsDt.length > 10)
        LocalDateTime.parse(obsDt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    else
        LocalDate.parse(obsDt).atStartOfDay()

fun ChecklistSummaryDto.toDomain(): ChecklistSummary = ChecklistSummary(
    subId = subId,
    locId = locId,
    userDisplayName = userDisplayName,
    numSpecies = numSpecies,
    obsDt = parseObsDate(obsDt)
)

fun ChecklistObservationDto.toDomain(): ChecklistObservation = ChecklistObservation(
    speciesCode = speciesCode,
    howMany = howManyStr?.toIntOrNull()
)

fun ChecklistDto.toDomain(): Checklist = Checklist(
    subId = subId,
    locId = locId,
    userDisplayName = userDisplayName,
    numSpecies = numSpecies,
    obsDt = parseObsDate(obsDt),
    obs = obs.map { it.toDomain() }
)
