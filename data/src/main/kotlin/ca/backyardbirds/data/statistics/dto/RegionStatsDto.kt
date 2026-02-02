package ca.backyardbirds.data.statistics.dto

import ca.backyardbirds.domain.model.RegionStats
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegionStatsDto(
    @SerialName("numChecklists") val numChecklists: Int,
    @SerialName("numContributors") val numContributors: Int,
    @SerialName("numSpecies") val numSpecies: Int
)

fun RegionStatsDto.toDomain(): RegionStats = RegionStats(
    numChecklists = numChecklists,
    numContributors = numContributors,
    numSpecies = numSpecies
)
