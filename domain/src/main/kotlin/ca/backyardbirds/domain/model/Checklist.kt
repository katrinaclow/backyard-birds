package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ChecklistSummary(
    val subId: String,
    val locId: String,
    val userDisplayName: String,
    val numSpecies: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val obsDt: LocalDateTime
)

@Serializable
data class Checklist(
    val subId: String,
    val locId: String,
    val userDisplayName: String,
    val numSpecies: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val obsDt: LocalDateTime,
    val obs: List<ChecklistObservation>
)

@Serializable
data class ChecklistObservation(
    val speciesCode: String,
    val howMany: Int?
)
