package ca.backyardbirds.data.statistics.dto

import ca.backyardbirds.domain.model.TopObserver
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopObserverDto(
    @SerialName("userDisplayName") val userDisplayName: String,
    @SerialName("numSpecies") val numSpecies: Int,
    @SerialName("numCompleteChecklists") val numChecklists: Int,
    @SerialName("rowNum") val rowNum: Int,
    @SerialName("userId") val userId: String
)

fun TopObserverDto.toDomain(): TopObserver = TopObserver(
    userDisplayName = userDisplayName,
    numSpecies = numSpecies,
    numChecklists = numChecklists,
    rowNum = rowNum,
    userId = userId
)
